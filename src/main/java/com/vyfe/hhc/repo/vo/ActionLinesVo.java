package com.vyfe.hhc.repo.vo;

import java.util.Map;

import com.vyfe.hhc.poker.ActionLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ActionLinesVo类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/18
 * Description: 行动线包装类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActionLinesVo {
    private Map<String, ActionLine> actionLines;
}
