package com.vyfe.hhc.poker.runner;

import java.util.Arrays;
import java.util.Objects;

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
        return 0;
    }
}
