package com.vyfe.hhc.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.vyfe.hhc.decoder.GGCashDecoder;
import com.vyfe.hhc.poker.constant.UserConstant;
import com.vyfe.hhc.poker.type.GameType;
import com.vyfe.hhc.repo.GGHandMsg;
import com.vyfe.hhc.repo.GGHandMsgRepo;
import com.vyfe.hhc.repo.GGSessionMsg;
import com.vyfe.hhc.repo.GGSessionMsgRepo;
import com.vyfe.hhc.system.HhcException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

/**
 * RecordDecodeTest类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: 解析手牌记录到Session和Hand对象
 */
@Configuration
@SpringBootApplication
public class RecordDecodeTest extends ScriptTpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordDecodeTest.class);
    @Autowired
    private GGCashDecoder ggCashDecoder;
    @Autowired
    private GGHandMsgRepo ggHandMsgRepo;
    @Autowired
    private GGSessionMsgRepo ggSessionMsgRepo;
    
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> taskRun(args);
    }
    
    public static void main(String[] args) {
        execute(args, RecordDecodeTest.class);
    }
    
    @Override
    @Transactional
    public void taskRun(String[] args) throws HhcException {
        var file = StringUtils.isBlank(args[0]) ? "/Users/chenyifei03/Downloads/GG20230129-0909 - RushAndCash4596429 - 0.01 - 0.02 - 6max.txt" : args[0];
        LOGGER.info("file name:{}", file);
        List<Object> fileContent = new ArrayList<>();
        String fileMd5 = StringUtils.EMPTY;
        try {
            File fileObj = new File(file);
            fileContent = FileUtils.readLines(fileObj);
            fileMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(fileObj));
        } catch (IOException e) {
            LOGGER.error("io err, pls retry");
        }
        LOGGER.info("file size: {}", fileContent.size());
        // split by double \r后，倒序排列
        List<List<String>> handsMap = new ArrayList<>();
        List<String> ctn = new ArrayList<>();
        for (Object line : fileContent) {
            if (StringUtils.isBlank(line.toString())) {
                if (ctn.size() > 0) {
                    handsMap.add(0, new ArrayList<>(ctn));
                    ctn.clear();
                }
            } else {
                ctn.add(line.toString());
            }
        }
        // 归纳session信息前去重
        if (!CollectionUtils.isEmpty(ggSessionMsgRepo.findByUidAndGameTypeAndFileMd5(UserConstant.GG_USER_ID_MAP.get("vyfe"),
                GameType.CASHRUSH, fileMd5))) {
            throw new HhcException("session repeat ,err");
        }
        GGSessionMsg session = new GGSessionMsg();
        session.setUid(UserConstant.GG_USER_ID_MAP.get("vyfe"));
        session.setGameType(GameType.CASHRUSH);
        session.setHands(handsMap.size());
        session.setFileMd5(fileMd5);
        session = ggSessionMsgRepo.save(session);
        // 为了去重，增加一个时间+源session文件md5的去重逻辑
        
        BigDecimal cashIn = BigDecimal.ZERO, cashOut = BigDecimal.ZERO,
                formerCash = BigDecimal.ZERO;
        int level = 0;
        LocalDateTime startTime = LocalDateTime.now();
        for (int i = 0; i < handsMap.size(); i++) {
            GGHandMsg handTest = ggCashDecoder.parseCashHand(handsMap.get(i), formerCash,
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
            if (i == handsMap.size() - 1) {
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
