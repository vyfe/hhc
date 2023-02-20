package com.vyfe.hhc.decoder.utils;

import java.math.BigDecimal;

/**
 * RegexUtil类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: 常用的匹配截取规则
 */
public class RegexUtil {
    /**
     * 转换类似"$3.04"到BigDecimal
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
}
