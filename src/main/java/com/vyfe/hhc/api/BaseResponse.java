package com.vyfe.hhc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * BaseResponse类.
 * <p>
 * User: chenyifei03
 * Date: 2023/3/1
 * Description:
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private Integer code;
    private String msg;
    private T data;
    
    public static <T> BaseResponse<T> withStatusAndInfoAndData(Integer status, String errorInfo, T arr) {
        return new BaseResponse<T>(status, errorInfo, arr);
    }
}
