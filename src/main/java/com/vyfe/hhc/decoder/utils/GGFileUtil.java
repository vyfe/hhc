package com.vyfe.hhc.decoder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.vyfe.hhc.poker.SessionMsg;
import com.vyfe.hhc.poker.constant.HoldemConstant;
import com.vyfe.hhc.system.HhcException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * GGFileUtil类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/21
 * Description:
 */
@Service("GG")
public class GGFileUtil implements FileParser {
    @Override
    public Pair<List<List<String>>, String> parseHandsFile(File file) throws HhcException {
        List<Object> fileContent;
        String fileMd5;
        try {
            fileContent = FileUtils.readLines(file);
            fileMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(file));
        } catch (IOException e) {
            throw new HhcException("io err, pls retry");
        }
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
        return Pair.of(handsMap, fileMd5);
    }
    
    @Override
    public SessionMsg parseTournamentOverviewFile(File file) throws HhcException {
        List<Object> fileContent;
        SessionMsg sessionMsg = new SessionMsg();
        try {
            fileContent = FileUtils.readLines(file);
        } catch (IOException e) {
            throw new HhcException("io err, pls retry");
        }
        for (Object line : fileContent) {
            // 每行匹配逻辑
            // line 1：以Tournament开始
            String str = line.toString();
            if (str.startsWith("Tournament") && str.contains("#")) {
                sessionMsg.setTournamentId(Long.valueOf(str.split(",")[0].split("#")[1]));
                sessionMsg.setTournamentName(str.split(",")[1].trim());
            }
            // line 2：买入
            if (str.startsWith("Buy-in")) {
                sessionMsg.setBuyInDollar(RegexUtil.cashStringToDecimal(str.split(":")[1]));
            }
            // line 3：参与人数
            if (str.endsWith("Players")) {
                sessionMsg.setPeopleAttend(Integer.valueOf(str.split(StringUtils.SPACE)[0]));
            }
            if (str.contains("started")) {
                sessionMsg.setStartTime(RegexUtil.ggTimeToLocalDateTime(str.split(StringUtils.SPACE)[2] +
                        StringUtils.SPACE + str.split(StringUtils.SPACE)[3]));
            }
            if (str.contains("Hero")) {
                sessionMsg.setRank(Integer.valueOf(
                        str.split(StringUtils.SPACE)[0].trim().replaceAll("[a-z].*", StringUtils.EMPTY)));
                sessionMsg.setCashOut(RegexUtil.cashStringToDecimal(str.split(",")[1].trim()));
            }
        }
        return sessionMsg;
    }
}
