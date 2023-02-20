package com.vyfe.hhc.system.type;

public interface ExceptionType {
    /**
     * 业务异常编码
     * @return
     */
    Integer getCode();
    
    /**
     * 异常描述性文字
     * @return
     */
    String getDesc();
}
