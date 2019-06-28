package com.example.spring.data.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * This configuration class configures transaction manager to be used.
 *
 * @author amit
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfiguration {

    /**
     * Spring data redis does not ships with it's own transaction maanager, we need to use existing
     * PlatformTransactionManager implementation like DataSourceTransactionManager or JpaTransactionManager
     * <p>
     * So, here we are configuring JDBC plantoform transaction manager.
     *
     * @param dataSource
     * @return
     */
    @Bean
    public PlatformTransactionManager dataSourceTransactionManager(final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * Embedded DataSource onl for DataSourceTransactionManager configuration.
     *
     * @return
     */
    @Bean
    public DataSource embeddedDataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    }
}
