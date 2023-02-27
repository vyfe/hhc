package com.vyfe.hhc.poker.type;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/15
 * Description: 扑克四种花色；
 * order仅用于并列输出排序，无其他意义
 */
@ToString
@AllArgsConstructor
public enum Decor {
    SPADE("s", 1),
    HEART("h", 2),
    CLUB("c", 3),
    DIAMOND("d", 4)
    ;
    
    @Getter
    private final String desc;
    
    @Getter
    private final Integer order;
    
    public static Decor getByDesc(String desc) {
        return Stream.of(values()).filter(f -> f.getDesc().equalsIgnoreCase(desc)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not valid decor: " + desc));
    }
}
