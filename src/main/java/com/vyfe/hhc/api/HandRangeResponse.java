package com.vyfe.hhc.api;

import java.util.List;

import com.vyfe.hhc.api.vo.HandStatistic;
import lombok.Data;

/**
 * HandRangeResponse类.
 * <p>
 * User: chenyifei03
 * Date: 2023/3/1
 * Description: 手牌分布
 */
@Data
public class HandRangeResponse {
    /**
     * 总手数
     */
    private Integer totalHands;
    /**
     * 总赢率
     */
    private Double winRate;
    
    private List<HandStatistic> handStatistics;
}
