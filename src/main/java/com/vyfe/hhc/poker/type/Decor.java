package com.vyfe.hhc.poker.type;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/15
 * Description: 扑克四种花色
 */
@ToString
public enum Decor {
    SPADE("s"),
    HEART("h"),
    CLUB("c"),
    DIAMOND("d")
    ;
    
    @Getter
    private String desc;
    
    Decor(String desc) {
        this.desc = desc;
    }
    
    public static Decor getByDesc(String desc) {
        return Stream.of(values()).filter(f -> f.getDesc().equalsIgnoreCase(desc)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not valid decor: " + desc));
    }
}
