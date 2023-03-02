package com.vyfe.hhc.script;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.hutool.core.io.FileUtil;
import com.vyfe.hhc.parse.GGCashImporter;
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
 * GGCashDecodeTest类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: 解析一个现金局手牌记录到Session和Hand对象
 */
@Configuration
public class GGCashRushDecodeTest extends ScriptTpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(GGCashRushDecodeTest.class);
    @Autowired
    private GGCashImporter cashImporter;
    
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> taskRun(args);
    }
    
    public static void main(String[] args) {
        execute(args, GGCashRushDecodeTest.class);
    }
    
    @Override
    @Transactional
    public void taskRun(String[] args) throws HhcException {
        var file = args.length <= 0 ? "/Users/chenyifei03/Downloads/GG20230129-0909 - RushAndCash4596429 - 0.01 - 0.02 - 6max.txt" : args[0];
        LOGGER.info("zip file name:{}", file);
        // cash文件导入逻辑
        try (ZipFile zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> zipEty = zip.entries();
            while (zipEty.hasMoreElements()) {
                ZipEntry entry = zipEty.nextElement();
                // zip中的文件
                File fileToImport = File.createTempFile("gg-cash-hands", ".txt");
                FileUtil.writeFromStream(zip.getInputStream(entry), fileToImport);
                // zip包解压后，各file依次导入
                cashImporter.startImport(fileToImport);
                // 导入后删除
                fileToImport.delete();
            }
        } catch (IOException e) {
            LOGGER.error("file download meeting io err:", e);
            throw new HhcException("file parse fail");
        }

    }
}
