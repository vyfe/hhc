package com.vyfe.hhc.poker.runner;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/25
 * Description: 德州成牌类型比较
 */
@AllArgsConstructor
public enum HoldemComboType {
    STRAIGHT_FLUSH(1, 1, 0, "同花顺"),
    FOUR(2, 1, 1, "四条"),
    FULLHOUSE(3, 2, 0, "葫芦"),
    FLUSH(4, 1, 4, "同花"),
    STRAIGHT(5, 1, 0, "顺子"),
    SET(6, 1, 2, "三条"),
    TWO_PAIR(7, 2, 1, "两对"),
    PAIR(8, 2, 3,"一对"),
    HIGH_CARD(9, 1, 4,"高张"),
    ;
    /**
     * 牌力排序
     */
    @Getter
    private int rank;
    /**
     * 同牌型下比较大小的维度，如：
     * 顺子之间只比大张，为1；
     * 同花之间最大可能是2（同一局内，最多可能比手中的两张同花色的大小）
     * 葫芦为2（3张的序号，一对的序号）
     * 三条为1（1张牌即可确定三条间的大小）
     * 以此类推
     */
    @Getter
    private int dim;
    /**
     * 牌型相同时，最多可能需要比较散牌的数量
     */
    @Getter
    private int pieceNum;
    @Getter
    private String desc;
}
