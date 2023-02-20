/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.vyfe.hhc.repo.converter;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.vyfe.hhc.poker.ActionLine;
import com.vyfe.hhc.poker.Card;
import com.vyfe.hhc.poker.type.ActionType;
import com.vyfe.hhc.repo.vo.ActionLinesVo;
import com.vyfe.hhc.system.util.JsonUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

@Converter
public class HeroHandsConverter implements AttributeConverter<Pair<Card, Card>, String> {
    @Override
    public String convertToDatabaseColumn(Pair<Card, Card> attribute) {
        // 简化为字符串:['4h','5c']
        return JsonUtils.toJson(Lists.newArrayList(attribute.getLeft().toUsualDesc(),
                attribute.getRight().toUsualDesc()));
    }
    
    @Override
    public Pair<Card, Card> convertToEntityAttribute(String dbData) {
        List<String> cardList = JsonUtils.fromJson(dbData, new TypeReference<>() {});
        assert cardList.size() == 2;
        return Pair.of(Card.parseUsualDesc(cardList.get(0)), Card.parseUsualDesc(cardList.get(1)));
    }
}
