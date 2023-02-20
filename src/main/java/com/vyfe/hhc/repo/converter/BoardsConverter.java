/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.vyfe.hhc.repo.converter;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.vyfe.hhc.poker.Card;
import com.vyfe.hhc.repo.vo.BoardsVo;
import com.vyfe.hhc.system.util.JsonUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.tuple.Pair;

@Converter
public class BoardsConverter implements AttributeConverter<BoardsVo, String> {
    @Override
    public String convertToDatabaseColumn(BoardsVo attribute) {
        // 简化为字符串:[['4h','5c'], ['3d, Tc']]之类的
        // 同样，默认其公共牌也是连续的(有2必有1，有3必有1，2)
        List<List<String>> boardList = new ArrayList<>();
        if (attribute.getBoard1() != null) {
            boardList.add(attribute.getBoard1().stream().map(Card::toUsualDesc).collect(Collectors.toList()));
            if (attribute.getBoard2() != null) {
                boardList.add(attribute.getBoard2().stream().map(Card::toUsualDesc).collect(Collectors.toList()));
                if (attribute.getBoard3() != null) {
                    boardList.add(attribute.getBoard3().stream().map(Card::toUsualDesc).collect(Collectors.toList()));
                }
            }
        }
        return JsonUtils.toJson(boardList);
    }
    
    @Override
    public BoardsVo convertToEntityAttribute(String dbData) {
        List<List<String>> boardList = JsonUtils.fromJson(dbData, new TypeReference<>() {});
        assert boardList.size() <= 3;
        BoardsVo vo = new BoardsVo();
        switch (boardList.size()) {
            case 3:
                vo.setBoard3(boardList.get(2).stream().map(Card::parseUsualDesc).collect(Collectors.toList()));
            case 2:
                vo.setBoard2(boardList.get(1).stream().map(Card::parseUsualDesc).collect(Collectors.toList()));
            case 1:
                vo.setBoard1(boardList.get(0).stream().map(Card::parseUsualDesc).collect(Collectors.toList()));
                break;
            default:
                break;
        }
        return vo;
    }
}
