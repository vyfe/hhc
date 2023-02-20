package com.vyfe.hhc.system;

/**
 * HhcException类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description:
 */
public class HhcException extends Exception {
    public HhcException() {
    }
    
    public HhcException(String message) {
        super(message);
    }
    
    public HhcException(String message, Throwable cause) {
        super(message, cause);
    }
}
