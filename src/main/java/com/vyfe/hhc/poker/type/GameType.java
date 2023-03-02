package com.vyfe.hhc.poker.type;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/15
 * Description: 游戏类型
 */
@AllArgsConstructor
public enum GameType {
    CASH(0), CASHRUSH(1), MTT(2);
    
    @Getter
    private final int code;
    
    public static GameType getByCode(Integer num) {
        return Stream.of(values()).filter(f -> f.getCode() == num).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not valid code: " + num));
    }
}
