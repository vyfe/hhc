package com.vyfe.hhc.poker;

import java.io.Serializable;
import java.util.List;

import com.vyfe.hhc.repo.vo.ActionVo;
import lombok.Data;

/**
 * ActionLine类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: 每人每局的行动线
 */
@Data
public class ActionLine implements Serializable {
    /**
     * 翻前行动(动作+当次投入的金额)
     */
    private List<ActionVo> preFlop;
    /**
     * 翻牌
     */
    private List<ActionVo> flop;
    /**
     * 转牌
     */
    private List<ActionVo> turn;
    /**
     * 河牌
     */
    private List<ActionVo> river;
}
