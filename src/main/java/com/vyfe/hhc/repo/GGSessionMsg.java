package com.vyfe.hhc.repo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vyfe.hhc.poker.type.GameType;
import com.vyfe.hhc.poker.type.MTTResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * GGSessionMsg类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/15
 * Description:
 */
@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name = "poker_session_gg", indexes = {
        @Index(name = "idx_uid_type", columnList = "uid, gameType"),
})
public class GGSessionMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户ID 目前仅自己，代号为1
     */
    private Integer uid;
    /**
     * session开始时间点
     */
    private LocalDateTime startTime;
    /**
     * session文件的md5值，用于避免重复导入
     */
    private String fileMd5;
    /**
     * 游戏类型
     */
    private GameType gameType;
    /**
     * 会话内总buyIn，单位为刀
     */
    private BigDecimal cashIn;
    /**
     * 会话内总结算额，单位为刀
     */
    private BigDecimal cashOut;
    /**
     * 总手数
     */
    private Integer hands;
    /**
     * 级别(单次买入或100BB对应刀)
     */
    private Integer level;
    /**
     * --后续字段为锦标赛专属
     * 总参与人数
     */
    private Integer peopleAttend;
    /**
     * 锦标赛名字
     */
    private String tournamentName;
    /**
     * 排名
     */
    private Integer rank;
    /**
     * 锦标赛ID
     */
    private Long tournamentId;
    /**
     * 锦标赛结果
     */
    private MTTResult resultType;
}
