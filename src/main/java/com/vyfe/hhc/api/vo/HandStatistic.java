package com.vyfe.hhc.api.vo;

import lombok.Data;

/**
 * HandStatistic类.
 * <p>
 * User: chenyifei03
 * Date: 2023/3/1
 * Description: 手牌分布
 */
@Data
public class HandStatistic {
    /**
     * 坐标轴，用于定位到前端表格
     * 范围AA(1, 1)~22(13, 13)
     */
    private int x;
    
    private int y;
    
    private int totalHand = 0;
    
    private int vpipHand = 0;
    
    private int winHand = 0;
    
    private double vpipRate;
    
    private double winRate;
    
    public void addTotal() {
        totalHand++;
    }
    
    public void addVpip() {
        vpipHand++;
    }
    
    public void addWin() {
        winHand++;
    }
}
