package com.vyfe.hhc.poker.runner;

import java.util.Arrays;

import com.vyfe.hhc.poker.Card;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * BestCombo类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/25
 * Description: 根据牌面得到的目前最好的组合，以及相关的大小比对信息
 */
@Data
@ToString
@EqualsAndHashCode
public class BestCombo implements Comparable<BestCombo> {
    private HoldemComboType type;
    /**
     * 长度根据type.dim变化
     */
    private Integer[] dim;
    /**
     * 相同牌型时用于比较的散牌，只记大小
     */
    private Integer[] pieceDim;
    
    private Card[] comboCards;
    
    @Override
    public int compareTo(BestCombo o) {
        // 按牌型->dim依次->pieceDim依次比较
        // 大于0时代表this更大
        if (!this.getType().equals(o.getType())) {
            return o.getType().getRank() - this.getType().getRank();
        } else if (!Arrays.equals(this.getDim(), o.getDim())) {
            for (int i = 0; i < this.getDim().length; i++) {
                if (!this.getDim()[i].equals(o.getDim()[i])) {
                    return this.getDim()[i] - o.getDim()[i];
                }
            }
            // 理论上不可达
            return 0;
        } else if (!Arrays.equals(this.getPieceDim(), o.getPieceDim())) {
            for (int i = 0; i < this.getPieceDim().length; i++) {
                if (!this.getPieceDim()[i].equals(o.getPieceDim()[i])) {
                    return this.getPieceDim()[i] - o.getPieceDim()[i];
                }
            }
            // 理论上不可达
            return 0;
        }
        return 0;
    }
}
