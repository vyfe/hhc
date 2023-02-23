package com.vyfe.hhc.poker.type;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Created with IntelliJ IDEA.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: 行动类型，先简单定义吧
 */
public enum ActionType {
    FOLD,
    CHECK,
    SHOW,
    // CALL类型
    LIMP,
    CALL,
    // OPEN-BET类型(翻前OPEN，翻后BET)
    OPEN,
    BET,
    // RAISE(RAISE1=翻前3BET)
    RAISE,
    RAISE2,
    RAISE3P,
    ALLIN,
    // 保险
    INSURANCE,
    ;
    
    /**
     * 主动投入的行动类型
     * @return
     */
    public static Set<ActionType> raiseActions() {
        return Sets.newHashSet(OPEN, BET, RAISE, RAISE2, RAISE3P, ALLIN);
    }
    
    /**
     * 被动行动（无投入底池）
     * @return
     */
    public static Set<ActionType> passiveActions() {
        return Sets.newHashSet(FOLD, CHECK, SHOW);
    }
}
