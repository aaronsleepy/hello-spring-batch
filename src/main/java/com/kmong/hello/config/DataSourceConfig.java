package com.kmong.hello.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix="spring.datasource.default.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties(prefix="spring.datasource.item.hikari")
    public HikariConfig itemHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.seller.hikari")
    public HikariConfig sellerHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @Primary
    public DataSource defaultDataSource() {
        return new LazyConnectionDataSourceProxy(new HikariDataSource(hikariConfig()));
    }

    @Bean
    public DataSource itemDataSource() {
        return new LazyConnectionDataSourceProxy(new HikariDataSource(itemHikariConfig()));
    }

    @Bean
    public DataSource sellerDataSource() {
        return new LazyConnectionDataSourceProxy(new HikariDataSource(sellerHikariConfig()));
    }
}
