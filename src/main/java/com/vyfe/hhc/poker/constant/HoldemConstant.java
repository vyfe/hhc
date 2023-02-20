package com.vyfe.hhc.poker.constant;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * HoldemConstant类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/15
 * Description:
 */
public class HoldemConstant {
    public static final String PLAYER_WORD_GG = "hero";
    
    public static final String FLOP_DEVIDE_WORD_GG = "FLOP";
    public static final String TURN_DEVIDE_WORD_GG = "TURN";
    public static final String RIVER_DEVIDE_WORD_GG = "RIVER";
    public static final String END_DEVIDE_WORD_GG = "SHOWDOWN";
    public static final String SUMMARY_DEVIDE_WORD_GG = "SUMMARY";
    
    /**
     * 不同人数时各座位称呼，第一位始终为SB；最大先支持12人桌
     */
    public static final Map<Integer, String[]> POSITION_DESC = new ImmutableMap.Builder<Integer, String[]>()
            .put(2, new String[]{"SB", "BB"})
            .put(3, new String[]{"SB", "BB", "BTN"})
            .put(4, new String[]{"SB", "BB", "CO", "BTN"})
            .put(5, new String[]{"SB", "BB", "UTG", "CO", "BTN"})
            .put(6, new String[]{"SB", "BB", "UTG", "HJ", "CO", "BTN"})
            .put(7, new String[]{"SB", "BB", "UTG", "MP", "HJ", "CO", "BTN"})
            .put(8, new String[]{"SB", "BB", "UTG", "UTG+1", "MP", "HJ", "CO", "BTN"})
            .put(9, new String[]{"SB", "BB", "UTG", "UTG+1", "MP1", "MP2", "HJ", "CO", "BTN"})
            .put(10, new String[]{"SB", "BB", "UTG", "UTG+1", "UTG+2", "MP1", "MP2", "HJ", "CO", "BTN"})
            .put(11, new String[]{"SB", "BB", "UTG", "UTG+1", "UTG+2", "MP1", "MP2", "LJ", "HJ", "CO", "BTN"})
            .put(12, new String[]{"SB", "BB", "UTG", "UTG+1", "UTG+2", "MP1", "MP2", "MP3", "LJ", "HJ", "CO", "BTN"})
            .build();
    
    /**
     * 四条街的序号
     */
    public static final int STREET_PREFLOP = 0;
    public static final int STREET_FLOP = 1;
    public static final int STREET_TURN = 2;
    public static final int STREET_RIVER = 3;
    public static final int STREET_SHOWDOWN = 4;
    /**
     * 分隔符->街数
     */
    public static final Map<String, Integer> GG_STREET_SEPERATE_MAP = new ImmutableMap.Builder<String, Integer>()
            .put(FLOP_DEVIDE_WORD_GG, STREET_FLOP)
            .put(TURN_DEVIDE_WORD_GG, STREET_TURN)
            .put(RIVER_DEVIDE_WORD_GG, STREET_RIVER)
            .put(END_DEVIDE_WORD_GG, STREET_SHOWDOWN)
            .build();
    
    /**
     * 行动单词
     */
    public static final String CALL_ACTION_GG = "calls";
    public static final String RAISE_ACTION_GG = "raises";
    public static final String FOLD_ACTION_GG = "folds";
    public static final String CHECK_ACTION_GG = "checks";
    public static final String BET_ACTION_GG = "bets";
    public static final String ALLIN_ACTION_GG = "all-in";
    public static final String SHOW_ACTION_GG = "shows";
}
