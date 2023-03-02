package com.vyfe.hhc.parse;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.vyfe.hhc.parse.utils.FileParser;
import com.vyfe.hhc.poker.constant.UserConstant;
import com.vyfe.hhc.poker.type.GameType;
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
 * Description: 单个cash文件的导入逻辑
 */
@Service
public class GGCashImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GGCashImporter.class);
    public static final Map<String, GameType> GG_CASH_GAMETYPE_MAP = new ImmutableMap.Builder<String, GameType>()
            .put("HD", GameType.CASH)
            .put("RC", GameType.CASHRUSH)
            .build();
    @Autowired
    private GGHandDecoder ggHandDecoder;
    @Autowired
    private GGHandMsgRepo ggHandMsgRepo;
    @Autowired
    private GGSessionMsgRepo ggSessionMsgRepo;
    @Autowired
    private FileParser ggFileParser;
    
    public void startImport(File fileObj) throws HhcException {
        Pair<List<List<String>>, String> fileMsg = ggFileParser.parseHandsFile(fileObj);
        // 对于普通桌和极速桌，还有个区分GameType逻辑
        String handIdPrefix = fileMsg.getLeft().get(0).get(0).split("#")[1].split(":")[0].substring(0, 2);
        GameType type = GG_CASH_GAMETYPE_MAP.get(handIdPrefix);
        
        // 为了去重，增加一个uid+文件类型+源session文件md5的去重逻辑
        if (!CollectionUtils.isEmpty(ggSessionMsgRepo.findByUidAndGameTypeAndFileMd5(UserConstant.GG_USER_ID_MAP.get("vyfe"),
                type, fileMsg.getRight()))) {
            LOGGER.error("session repeat ,jump this");
            return;
        }
        GGSessionMsg session = new GGSessionMsg();
        session.setUid(UserConstant.GG_USER_ID_MAP.get("vyfe"));
        session.setGameType(type);
        session.setHands(fileMsg.getLeft().size());
        session.setFileMd5(fileMsg.getRight());
        session = ggSessionMsgRepo.save(session);
        BigDecimal cashIn = BigDecimal.ZERO, cashOut = BigDecimal.ZERO,
                formerCash = BigDecimal.ZERO;
        int level = 0;
        LocalDateTime startTime = LocalDateTime.now();
        for (int i = 0; i < fileMsg.getLeft().size(); i++) {
            GGHandMsg handTest = ggHandDecoder.parseCashHand(fileMsg.getLeft().get(i), formerCash,
                    session.getId());
            LOGGER.info("hands parse result: {}", handTest);
            formerCash = handTest.getChipsAfter();
            cashIn = cashIn.add(handTest.getChipsSupply());
            ggHandMsgRepo.save(handTest);
            if (i == 0) {
                // 解析session必填信息
                startTime = handTest.getHandTime();
                // 对cash而言级别=100BB
                level = handTest.getBbSize().multiply(BigDecimal.valueOf(100)).intValue();
            }
            if (i == fileMsg.getLeft().size() - 1) {
                cashOut = handTest.getChipsAfter();
            }
        }
        // 统计后更新现金局概要
        session.setStartTime(startTime);
        session.setLevel(level);
        session.setCashIn(cashIn);
        session.setCashOut(cashOut);
        ggSessionMsgRepo.save(session);
    }
}
