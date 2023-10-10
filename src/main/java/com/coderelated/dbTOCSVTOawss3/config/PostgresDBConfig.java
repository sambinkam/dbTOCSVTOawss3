package com.coderelated.dbTOCSVTOawss3.config;

import java.util.logging.LogManager;

import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.coderelated.dbTOCSVTOawss3.model.Users;

@Configuration
public class PostgresDBConfig {
	
//	Logger logger= LogManager.getLogger(PostgresDBConfig.class);
	
	@Bean
	@ConfigurationProperties("spring.datasource.postgres")
	public DataSourceProperties postgresDatasourceProperties() {
		return new DataSourceProperties();
	}
	
	@Bean
	@Primary
	public DataSource postgresDataSource() {
		System.out.println(postgresDatasourceProperties().getUsername()+";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");
		return postgresDatasourceProperties().initializeDataSourceBuilder().build();
	}

}
