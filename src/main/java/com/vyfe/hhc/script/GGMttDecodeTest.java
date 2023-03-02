package com.vyfe.hhc.script;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.hutool.core.io.FileUtil;
import com.vyfe.hhc.parse.GGMttImporter;
import com.vyfe.hhc.system.HhcException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
            throw new HhcException("not right: must have 2 file path pair for a tournament:[overview file zip] [hands file zip]");
        }
        Map<String, Pair<File, File>> overViewMap = new HashMap<>();
        // 概要
        try (ZipFile zip = new ZipFile(args[0])) {
            Enumeration<? extends ZipEntry> zipEty = zip.entries();
            while (zipEty.hasMoreElements()) {
                ZipEntry entry = zipEty.nextElement();
                // zip中的文件
                File fileToImport = File.createTempFile("gg-mtt-overview", ".txt");
                FileUtil.writeFromStream(zip.getInputStream(entry), fileToImport);
                // 解析出关联str，放入map
                String tId = entry.getName().split("#")[1].split(" ")[0];
                overViewMap.put(tId, MutablePair.of(fileToImport, null));
            }
        } catch (IOException e) {
            LOGGER.error("read overview zip meeting io err:", e);
            throw new HhcException("zip parse fail");
        }
        // 详情
        try (ZipFile zip = new ZipFile(args[1])) {
            Enumeration<? extends ZipEntry> zipEty = zip.entries();
            while (zipEty.hasMoreElements()) {
                ZipEntry entry = zipEty.nextElement();
                // zip中的文件
                File fileToImport = File.createTempFile("gg-mtt-record", ".txt");
                FileUtil.writeFromStream(zip.getInputStream(entry), fileToImport);
                // 文件中的第一行，解析出tid
                List detailLines = FileUtils.readLines(fileToImport);
                Object firstLine = detailLines.get(0);
                String tId = firstLine.toString().split("#")[2].split(",")[0];
                if (overViewMap.containsKey(tId)) {
                    // 一个overview可能对应多个文件，append到指定文件
                    if (overViewMap.get(tId).getValue() != null) {
                        FileUtil.appendLines(detailLines, overViewMap.get(tId).getValue(),
                                StandardCharsets.UTF_8);
                    } else {
                        overViewMap.get(tId).setValue(fileToImport);
                    }
                } else {
                    LOGGER.error("tid:{}, not exist overview file", tId);
                }
            }
        } catch (IOException e) {
            LOGGER.error("read overview zip meeting io err:", e);
            throw new HhcException("zip parse fail");
        }
        // mtt的文件pair导入逻辑
        overViewMap.forEach((tid, filePair) -> {
            try {
                mttImporter.startImport(filePair.getLeft(), filePair.getRight());
                filePair.getLeft().delete();
                filePair.getRight().delete();
            } catch (HhcException e) {
                LOGGER.error("file download meeting io err:", e);
            }
        });
        // todo 理论上要对使用过的垃圾文件做清理
        
        // todo 对于有day2的mtt chips如何暂存?只能等我打进去才能收集案例了
    }
}
