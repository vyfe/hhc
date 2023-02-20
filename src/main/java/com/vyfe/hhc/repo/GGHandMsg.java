package com.vyfe.hhc.repo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.vyfe.hhc.poker.ActionLine;
import com.vyfe.hhc.poker.Card;
import com.vyfe.hhc.poker.type.ActionType;
import com.vyfe.hhc.poker.type.GameType;
import com.vyfe.hhc.repo.converter.ActionLinesConverter;
import com.vyfe.hhc.repo.converter.BoardsConverter;
import com.vyfe.hhc.repo.converter.HeroHandsConverter;
import com.vyfe.hhc.repo.vo.ActionLinesVo;
import com.vyfe.hhc.repo.vo.BoardsVo;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

/**
 * HandMsg类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/15
 * Description: 每手牌，牌局记录解析到该类
 */
@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name = "poker_hand_gg")
public class GGHandMsg implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 牌局ID
     */
    private String handId;
    /**
     * 关联到的sessionId
     */
    private Long sessionId;
    /**
     * 己方手牌
     */
    @Column(columnDefinition = "varchar(30)")
    @Convert(converter = HeroHandsConverter.class)
    private Pair<Card, Card> heroHands;
    /**
     * 发出的公共牌(可能多轮跑马)
     */
    @Column(columnDefinition = "varchar(256)")
    @Convert(converter = BoardsConverter.class)
    private BoardsVo boardHands;
    /**
     * 位置, 0为SB, 1为BB，依次类推
     */
    private int position;
    /**
     * 人数
     */
    private int chairs;
    /**
     * 开始时间点
     */
    private LocalDateTime handTime;
    /**
     * 大盲大小，MTT按chips，CASH按cent(1$=100)
     */
    private BigDecimal bbSize = BigDecimal.ZERO;
    /**
     * 小盲大小
     */
    private BigDecimal sbSize = BigDecimal.ZERO;
    /**
     * 当前剩余筹码
     */
    private BigDecimal chips = BigDecimal.ZERO;
    /**
     * 是否主动入池
     */
    private boolean isVpip = false;
    /**
     * 入池类型（冷跟,bet,call,limp等）
     */
    private ActionType vpipType;
    /**
     * 是否得到底池
     */
    private boolean winPot = false;
    /**
     * 局后剩余筹码
     */
    private BigDecimal chipsAfter = BigDecimal.ZERO;
    /**
     * 局间补充/提现的筹码
     */
    private BigDecimal chipsSupply = BigDecimal.ZERO;
    /**
     * 每个玩家的行动线
     */
    @Column(columnDefinition = "varchar(2048)")
    @Convert(converter = ActionLinesConverter.class)
    private ActionLinesVo actionLines;
}
