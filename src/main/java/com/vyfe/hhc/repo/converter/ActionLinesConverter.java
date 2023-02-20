/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.vyfe.hhc.repo.converter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vyfe.hhc.poker.ActionLine;
import com.vyfe.hhc.repo.vo.ActionLinesVo;
import com.vyfe.hhc.repo.vo.ActionVo;
import com.vyfe.hhc.system.util.JsonUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.CollectionUtils;

@Converter
public class ActionLinesConverter implements AttributeConverter<ActionLinesVo, String> {
    @Override
    public String convertToDatabaseColumn(ActionLinesVo attribute) {
        // 简化为二维数组的map{uid:[[preflop action][flop action]..]}
        Map<String, List<List<ActionVo>>> toJsonMap = new HashMap<>();
        for (Map.Entry<String, ActionLine> uidAndAction : attribute.getActionLines().entrySet()) {
            List<List<ActionVo>> actionListSimp = new ArrayList<>();
            ActionLine actionList = uidAndAction.getValue();
            // 因为行动是向后连续的，所以此处嵌套处理
            if (actionList.getPreFlop() != null ) {
                actionListSimp.add(actionList.getPreFlop());
                if (actionList.getFlop() != null ) {
                    actionListSimp.add(actionList.getFlop());
                    if (actionList.getTurn() != null ) {
                        actionListSimp.add(actionList.getTurn());
                        if (actionList.getRiver() != null ) {
                            actionListSimp.add(actionList.getRiver());
                        }
                    }
                }
            }
            toJsonMap.put(uidAndAction.getKey(), actionListSimp);
        }
        return JsonUtils.toJson(toJsonMap);
    }
    
    @Override
    public ActionLinesVo convertToEntityAttribute(String dbData) {
        Map<String, List<List<ActionVo>>> jsonMap =
                JsonUtils.fromJson(dbData, new TypeReference<>() {});
        Map<String, ActionLine> actionLines = new HashMap<>();
        jsonMap.forEach((uid, simpList) -> {
            if (!CollectionUtils.isEmpty(simpList)) {
                ActionLine actionObj = new ActionLine();
                switch (simpList.size()) {
                    case 4:
                        actionObj.setRiver(simpList.get(3));
                    case 3:
                        actionObj.setTurn(simpList.get(2));
                    case 2:
                        actionObj.setFlop(simpList.get(1));
                    case 1:
                        actionObj.setPreFlop(simpList.get(0));
                        break;
                    default:
                        break;
                }
                actionLines.put(uid, actionObj);
            }
        });
        return new ActionLinesVo(actionLines);
    }
}
