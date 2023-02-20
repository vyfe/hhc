package com.vyfe.hhc.repo.vo;

import java.math.BigDecimal;

import com.vyfe.hhc.poker.type.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ActionVo类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/19
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionVo {
    /**
     * 行动类型,actionType
     */
    private ActionType at;
    /**
     * put-in，投入筹码
     */
    private BigDecimal pi;
}
