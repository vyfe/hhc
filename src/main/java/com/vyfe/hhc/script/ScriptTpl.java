package com.vyfe.hhc.script;

import com.vyfe.hhc.system.HhcAppCnxt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * ScriptTpl类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: 脚本启动类
 */
public abstract class ScriptTpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptTpl.class);
    
    public static void execute(String[] args, Class clazz) {
        SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder();
        SpringApplication application = applicationBuilder.sources(HhcAppCnxt.class).sources(clazz)
                .web(WebApplicationType.NONE).build();
        int retCode = 0;
        try {
            application.run(args);
        } catch (Exception e) {
            e.printStackTrace();
            retCode = 1;
        }
        LOGGER.info("task:{} retCode:{}", clazz.getName(), retCode);
        System.exit(retCode);
    }
    
    protected abstract void taskRun(String[] args) throws Exception;
}
