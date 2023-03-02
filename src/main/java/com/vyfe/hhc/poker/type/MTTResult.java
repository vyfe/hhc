package com.vyfe.hhc.poker.type;

import java.math.BigDecimal;

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
    
    public static MTTResult getResult(Integer rank, Integer peopleAttend, BigDecimal cash) {
        if (rank == 1) {
            return CHAMPION;
        } else if (rank < 10) {
            return FT;
        } else if (cash.compareTo(BigDecimal.ZERO) != 0) {
            return CASH;
        } else if ((double) rank / (double)peopleAttend <= 0.25D) {
            return P25;
        } else if ((double) rank / (double)peopleAttend <= 0.5D) {
            return P50;
        } else {
            return OUT_OF_50;
        }
    }
    
}
