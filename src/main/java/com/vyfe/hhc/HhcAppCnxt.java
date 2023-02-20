package com.vyfe.hhc;

import javax.sql.DataSource;

import java.util.concurrent.TimeUnit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * HhcAppCnxt类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description:
 */
@Configuration
@ComponentScan(basePackages = {"com.vyfe.hhc"})
@PropertySource({"classpath:hhc.properties", "classpath:database.properties"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class HhcAppCnxt {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${spring.datasource.url}")
    private String url;
    
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        logger.info("create data source");
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl(url);
        config.addDataSourceProperty("user", "");
        config.addDataSourceProperty("password", "");
        config.setPoolName("POOL_NAME");
        // 一个连接闲置状态的最大时长（毫秒），超时则被释放（retired）
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(10));
        // 一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired）
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(30));
        // 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(20));
        // 连接被占用的超时时间，单位毫秒，默认为0，即禁用连接泄露检测
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(20));
        DataSource dataSource = new HikariDataSource(config);
        logger.info("choose database url is: " + url);
        return dataSource;
    }
    
    @Bean
    public EntityManagerFactory entityManagerFactory(@Autowired DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(jpaVendorAdapter());
        factory.setPackagesToScan("com.vyfe.hhc.repo");
        factory.setDataSource(dataSource);
        // Will be 'validate' if not specify explicitly
        factory.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", "update");
        factory.getJpaPropertyMap().put("hibernate.show_sql", true);
        // factory.getJpaPropertyMap().put("hibernate.show_sql", true);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
    
    @Bean
    @Autowired
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }
    
    @Bean
    @Autowired
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate;
    }
    
    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform("org.hibernate.community.dialect.SQLiteDialect");
        return jpaVendorAdapter;
    }
}
