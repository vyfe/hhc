package com.vyfe.hhc.parse.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * RegexUtil类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: 常用的匹配截取规则
 */
public class RegexUtil {
    /**
     * 转换类似"[CT]?$3.04"到BigDecimal
     * @param cashStr
     * @return
     */
    public static BigDecimal cashStringToDecimal(String cashStr) {
        return BigDecimal.valueOf(Double.parseDouble(
                cashStr.split("\\$")[1]));
    }
    
    /**
     * 转换逗号分隔符如"3,000,001"筹码到BigDecimal
     * @param chipStr
     * @return
     */
    public static BigDecimal chipStringToDecimal(String chipStr) {
        return BigDecimal.valueOf(Integer.parseInt(chipStr.replace(",", "")));
    }
    
    public static LocalDateTime ggTimeToLocalDateTime(String timeStr) {
        return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
    
    public static String lastSplitPiece(String strToSplit, String reg) {
        return strToSplit.split(reg)[strToSplit.split(reg).length - 1].trim();
    }
    
}
