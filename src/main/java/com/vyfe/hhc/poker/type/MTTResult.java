package com.vyfe.hhc.poker.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/20
 * Description: 锦标赛结果类型
 */
@AllArgsConstructor
@ToString
public enum MTTResult {
    OUT_OF_50("50%以外"),
    P50("50%以内"),
    P25("25%以内"),
    CASH("钱圈"),
    FT("final table"),
    CHAMPION("夺冠"),
    ;
    
    @Getter
    private String desc;
    
}
