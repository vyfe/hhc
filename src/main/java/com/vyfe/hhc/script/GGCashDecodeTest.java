package com.vyfe.hhc.script;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.vyfe.hhc.decoder.GGCashDecoder;
import com.vyfe.hhc.decoder.utils.FileParser;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * GGCashDecodeTest类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: 解析一个现金局手牌记录到Session和Hand对象
 */
@Configuration
public class GGCashDecodeTest extends ScriptTpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(GGCashDecodeTest.class);
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
        execute(args, GGCashDecodeTest.class);
    }
    
    @Override
    @Transactional
    public void taskRun(String[] args) throws HhcException {
        var file = args.length <= 0 ? "/Users/chenyifei03/Downloads/GG20230129-0909 - RushAndCash4596429 - 0.01 - 0.02 - 6max.txt" : args[0];
        LOGGER.info("file name:{}", file);
        File fileObj = new File(file);
        Pair<List<List<String>>, String> fileMsg = ggFileParser.parseHandsFile(fileObj);
        // 为了去重，增加一个uid+文件类型+源session文件md5的去重逻辑
        if (!CollectionUtils.isEmpty(ggSessionMsgRepo.findByUidAndGameTypeAndFileMd5(UserConstant.GG_USER_ID_MAP.get("vyfe"),
                GameType.CASHRUSH, fileMsg.getRight()))) {
            throw new HhcException("session repeat ,err");
        }
        GGSessionMsg session = new GGSessionMsg();
        session.setUid(UserConstant.GG_USER_ID_MAP.get("vyfe"));
        session.setGameType(GameType.CASHRUSH);
        session.setHands(fileMsg.getLeft().size());
        session.setFileMd5(fileMsg.getRight());
        session = ggSessionMsgRepo.save(session);
        BigDecimal cashIn = BigDecimal.ZERO, cashOut = BigDecimal.ZERO,
                formerCash = BigDecimal.ZERO;
        int level = 0;
        LocalDateTime startTime = LocalDateTime.now();
        for (int i = 0; i < fileMsg.getLeft().size(); i++) {
            GGHandMsg handTest = ggCashDecoder.parseCashHand(fileMsg.getLeft().get(i), formerCash,
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
