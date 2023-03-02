package com.vyfe.hhc.parse;

import java.io.File;
import java.util.List;

import com.vyfe.hhc.parse.utils.FileParser;
import com.vyfe.hhc.poker.SessionMsg;
import com.vyfe.hhc.poker.constant.UserConstant;
import com.vyfe.hhc.poker.mapper.SessionMapper;
import com.vyfe.hhc.poker.type.GameType;
import com.vyfe.hhc.poker.type.MTTResult;
import com.vyfe.hhc.repo.GGHandMsg;
import com.vyfe.hhc.repo.GGHandMsgRepo;
import com.vyfe.hhc.repo.GGSessionMsg;
import com.vyfe.hhc.repo.GGSessionMsgRepo;
import com.vyfe.hhc.system.HhcException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * GGCashImporter类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/23
 * Description: 一对mtt文件(overview+hands)的导入逻辑
 */
@Service
public class GGMttImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GGMttImporter.class);
    @Autowired
    private GGHandDecoder ggHandDecoder;
    @Autowired
    private GGHandMsgRepo ggHandMsgRepo;
    @Autowired
    private GGSessionMsgRepo ggSessionMsgRepo;
    @Autowired
    private FileParser ggFileParser;
    
    public void startImport(File overViewFile, File handsFile) throws HhcException {
        SessionMsg overViewMsg = ggFileParser.parseTournamentOverviewFile(overViewFile);
        LOGGER.info("over view msg:{}", overViewMsg);
        // 对锦标赛使用uid+type+tournamentId去重
        if (!CollectionUtils.isEmpty(ggSessionMsgRepo.findByUidAndGameTypeAndTournamentId(
                UserConstant.GG_USER_ID_MAP.get("vyfe"), GameType.MTT, overViewMsg.getTournamentId()))) {
            LOGGER.error("session repeat ,jump this");
            return;
        }
        GGSessionMsg sessionMsg = SessionMapper.INSTANCE.msgToSession(overViewMsg);
        // 结果类型, 买入额
        sessionMsg.setResultType(MTTResult.getResult(overViewMsg.getRank(), overViewMsg.getPeopleAttend(),
                overViewMsg.getCashOut()));
        sessionMsg.setLevel(overViewMsg.getBuyInDollar().intValue());
        sessionMsg.setGameType(GameType.MTT);
        // 手数，解析handsFile
        Pair<List<List<String>>, String> fileMsg = ggFileParser.parseHandsFile(handsFile);
        sessionMsg.setHands(fileMsg.getLeft().size());
        sessionMsg.setUid(UserConstant.GG_USER_ID_MAP.get("vyfe"));
        sessionMsg.setFileMd5(fileMsg.getRight());
        sessionMsg = ggSessionMsgRepo.save(sessionMsg);
        for (int i = 0; i < fileMsg.getLeft().size(); i++) {
            GGHandMsg handTest = ggHandDecoder.parseMTTHand(fileMsg.getLeft().get(i), sessionMsg.getId());
            LOGGER.info("hands parse result: {}", handTest);
            ggHandMsgRepo.save(handTest);
        }
    }
}
