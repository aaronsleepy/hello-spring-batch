package com.kmong.hello.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class JpaConfig {
    private static final String BASE_PACKAGE = "com.kmong.hello";

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean itemEntityManagerFactory(
            @Qualifier("itemDataSource")DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPersistenceUnitName("item");
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(jpaVendorAdapters());
        emf.setPackagesToScan(BASE_PACKAGE);
        emf.setJpaProperties(jpaProperties());

        return emf;
    }

    @Bean
    public PlatformTransactionManager itemTransactionManager(
            @Qualifier("itemEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        jpaTransactionManager.setJpaDialect(new HibernateJpaDialect());

        return jpaTransactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean sellerEntityManagerFactory(
            @Qualifier("sellerDataSource")DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPersistenceUnitName("seller");
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(jpaVendorAdapters());
        emf.setPackagesToScan(BASE_PACKAGE);
        emf.setJpaProperties(jpaProperties());

        return emf;
    }

    @Bean
    public PlatformTransactionManager sellerTransactionManager(
            @Qualifier("sellerEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        jpaTransactionManager.setJpaDialect(new HibernateJpaDialect());

        return jpaTransactionManager;
    }



    private static Properties jpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
//        properties.setProperty("hibernate.use_sql_comments", "false");
//        properties.setProperty("hibernate.globally_quoted_identifiers", "true");
//
//        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
//
//        properties.setProperty("hibernate.jdbc.batch_size", "5000");
//        properties.setProperty("hibernate.order_inserts", "true");
//        properties.setProperty("hibernate.order_updates", "true");
//        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
//
//        properties.setProperty("spring.jpa.hibernate.jdbc.batch_size", "5000");
//        properties.setProperty("spring.jpa.hibernate.order_inserts", "true");
//        properties.setProperty("spring.jpa.hibernate.order_updates", "true");
//
//        properties.setProperty("spring.jpa.hibernate.jdbc.batch_versioned_data", "true");
//        properties.setProperty("spring.jpa.properties.hibernate.jdbc.batch_size", "5000");
//        properties.setProperty("spring.jpa.properties.hibernate.order_inserts", "true");
//        properties.setProperty("spring.jpa.properties.hibernate.order_updates", "true");
//        properties.setProperty("spring.jpa.properties.hibernate.jdbc.batch_versioned_data", "true");
//
        return properties;
    }

    private static JpaVendorAdapter jpaVendorAdapters() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return hibernateJpaVendorAdapter;
    }
}
