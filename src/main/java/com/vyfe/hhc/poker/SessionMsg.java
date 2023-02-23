package com.vyfe.hhc.poker;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

/**
 * SessionMsg类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/21
 * Description:
 */
@Data
@ToString
public class SessionMsg {
    private Long tournamentId;
    
    private String tournamentName;
    /**
     * 买入
     */
    private BigDecimal buyInDollar;
    
    private Integer peopleAttend;
    
    private LocalDateTime startTime;
    
    private Integer rank;
    
    private BigDecimal cashOut;
}
