package com.vyfe.hhc.poker.type;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CardNumber类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/15
 * Description:
 */
@AllArgsConstructor
public enum CardNumber {
    ACE(1, "A", 13),
    TWO(2, "2", 1),
    THREE(3, "3", 2),
    FOUR(4, "4", 3),
    FIVE(5, "5", 4),
    SIX(6, "6", 5),
    SEVEN(7, "7", 6),
    EIGHT(8, "8", 7),
    NINE(9, "9", 8),
    TEN(10, "T", 9),
    JACK(11, "J", 10),
    QUEEN(12, "Q", 11),
    KING(13, "K", 12),
    ;
    /**
     * 按大小排序(2->A)
     */
    private static final List<CardNumber> orderList;
    
    @Getter
    private int number;
    @Getter
    private String desc;
    /**
     * 用于比大小
     */
    @Getter
    private int order;
    
    static {
        orderList = Arrays.stream(values()).sorted(Comparator.comparing(CardNumber::getOrder))
                .collect(Collectors.toList());
    }
    
    public static CardNumber getByDesc(String desc) {
        return Stream.of(values()).filter(f -> f.getDesc().equalsIgnoreCase(desc)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not valid Code: " + desc));
    }
    
    public static CardNumber getByNumber(Integer num) {
        return Stream.of(values()).filter(f -> f.getNumber() == num).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not valid Num: " + num));
    }
    
    public static CardNumber getByOrder(Integer num) {
        // 防止越界
        return num < 1 ? orderList.get(orderList.size() + num - 1) : orderList.get(num - 1);
    }
    
    public static List<CardNumber> getOrderList() {
        return orderList;
    }
}
