package com.vyfe.hhc.poker.constant;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * UserConstant类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/20
 * Description:
 */
public class UserConstant {
    /**
     * 先用写死的map代替用户注册
     */
    public static final Map<String, Integer> GG_USER_ID_MAP = new ImmutableMap.Builder<String, Integer>()
            .put("vyfe", 1)
            .build();
}
