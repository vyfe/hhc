package com.vyfe.hhc.poker;

import java.io.Serializable;

import com.vyfe.hhc.poker.type.CardNumber;
import com.vyfe.hhc.poker.type.Decor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Card类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/15
 * Description: 手牌
 */
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Card implements Serializable {
    private CardNumber number;
    
    private Decor decor;
    
    public Card(CardNumber number, Decor decor) {
        this.number = number;
        this.decor = decor;
    }
    
    /**
     * 解析通用描述为Card对象
     * @param usualDesc
     */
    public static Card parseUsualDesc(String usualDesc) {
        if (usualDesc.length() != 2) {
            throw new IllegalArgumentException("wrong usual card desc:" + usualDesc);
        }
        return new Card(CardNumber.getByDesc(usualDesc.substring(0, 1)),
                Decor.getByDesc(usualDesc.substring(1)));
    }
    
    public CardNumber getNumber() {
        return number;
    }
    
    public Decor getDecor() {
        return decor;
    }
    
    /**
     * 转化为通用字符串
     * @return
     */
    public String toUsualDesc() {
        return this.number.getDesc() + this.decor.getDesc();
    }
    
}
