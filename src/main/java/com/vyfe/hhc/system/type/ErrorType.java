package com.vyfe.hhc.system.type;

/**
 * Created with IntelliJ IDEA.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description:
 */
public enum ErrorType implements ExceptionType {
    SYSTEM_BUSY("系统繁忙", 51),
    FAIL("失败", 99);
    private String desc;
    private Integer code;
    
    ErrorType(String desc, Integer code) {
        this.desc = desc;
        this.code = code;
    }
    
    @Override
    public Integer getCode() {
        return null;
    }
    
    @Override
    public String getDesc() {
        return null;
    }
}
