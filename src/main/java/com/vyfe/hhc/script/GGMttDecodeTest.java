package com.vyfe.hhc.script;

import java.io.File;

import com.vyfe.hhc.parse.GGMttImporter;
import com.vyfe.hhc.system.HhcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

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
    private GGMttImporter mttImporter;
    
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
        // 两个zip包解压后，各file首先匹配成Pair后依次导入
        // mtt的文件pair导入逻辑
        mttImporter.startImport(new File(args[0]), new File(args[1]));
    }
}
