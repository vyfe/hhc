package com.vyfe.hhc.script;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.vyfe.hhc.decoder.GGCashDecoder;
import com.vyfe.hhc.decoder.utils.FileParser;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * GGMttDecodeTest类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: 解析一个锦标赛概述+一个锦标赛手牌记录到Session和Hand对象
 */
@Configuration
public class GGMttDecodeTest extends ScriptTpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(GGMttDecodeTest.class);
    @Autowired
    private GGCashDecoder ggCashDecoder;
    @Autowired
    private GGHandMsgRepo ggHandMsgRepo;
    @Autowired
    private GGSessionMsgRepo ggSessionMsgRepo;
    @Autowired
    private FileParser ggFileParser;
    
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> taskRun(args);
    }
    
    public static void main(String[] args) {
        execute(args, GGMttDecodeTest.class);
    }
    
    @Override
    @Transactional
    public void taskRun(String[] args) throws HhcException {
        if (args.length < 2) {
            throw new HhcException("not right: must have 2 file path pair for a tournament:[overview file] [hands file]");
        }
        var overViewFile = new File(args[0]);
        var handsFile = new File(args[1]);
        SessionMsg overViewMsg = ggFileParser.parseTournamentOverviewFile(overViewFile);
        LOGGER.info("over view msg:{}", overViewMsg);
        // 对锦标赛使用uid+type+tournamentId去重
        if (!CollectionUtils.isEmpty(ggSessionMsgRepo.findByUidAndGameTypeAndTournamentId(
                UserConstant.GG_USER_ID_MAP.get("vyfe"), GameType.MTT, overViewMsg.getTournamentId()))) {
            throw new HhcException("session repeat ,err");
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
            GGHandMsg handTest = ggCashDecoder.parseNoRebuyMTTHand(fileMsg.getLeft().get(i), sessionMsg.getId());
            LOGGER.info("hands parse result: {}", handTest);
            ggHandMsgRepo.save(handTest);
        }
    }
}
