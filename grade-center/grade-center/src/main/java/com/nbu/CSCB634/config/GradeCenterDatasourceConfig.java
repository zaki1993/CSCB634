package com.nbu.CSCB634.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Profile("!test")
@EnableTransactionManagement
@EntityScan("com.nbu.CSCB634.model")
@EnableJpaRepositories(
        basePackages = "com.nbu.CSCB634.repository",
        entityManagerFactoryRef = "jpaEntityManagerFactory",
        transactionManagerRef = "jpaTransactionManager")
public class GradeCenterDatasourceConfig {

    private final Environment env;

    public GradeCenterDatasourceConfig(Environment env) {
        this.env = env;
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.grade-center")
    public DataSourceProperties jpaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.grade-center")
    public DataSource jpaDataSource() {
        return jpaDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = "jpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));

        return builder
                .dataSource(jpaDataSource())
                .packages("com.nbu.CSCB634.model")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager jpaTransactionManager(
            final @Qualifier("jpaEntityManagerFactory") LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory) {
        return new JpaTransactionManager(jpaEntityManagerFactory.getObject());
    }
}
