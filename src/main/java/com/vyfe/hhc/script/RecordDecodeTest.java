package com.vyfe.hhc.script;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.vyfe.hhc.decoder.GGCashDecoder;
import com.vyfe.hhc.repo.GGHandMsg;
import com.vyfe.hhc.repo.GGHandMsgRepo;
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
    private GGHandMsgRepo GGHandMsgRepo;
    @Autowired
    private TransactionTemplate transactionTemplate;
    
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> taskRun(args);
    }
    
    public static void main(String[] args) {
        execute(args, RecordDecodeTest.class);
    }
    
    @Override
    @Transactional
    public void taskRun(String[] args) {
        var file = StringUtils.isBlank(args[0]) ? "/Users/chenyifei03/Downloads/GG20230129-0909 - RushAndCash4596429 - 0.01 - 0.02 - 6max.txt" : args[0];
        LOGGER.info("file name:{}", file);
        List<Object> fileContent = new ArrayList<>();
        try {
            fileContent = FileUtils.readLines(new File(file));
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
        transactionTemplate.execute(ex -> {
            BigDecimal formerCash = BigDecimal.ZERO;
            for (List<String> strings : handsMap) {
                GGHandMsg handTest = ggCashDecoder.parseCashHand(strings, formerCash);
                LOGGER.info("hands parse result: {}", handTest);
                formerCash = handTest.getChipsAfter();
                GGHandMsgRepo.save(handTest);
            }
            return 0;
        });
    }
}
