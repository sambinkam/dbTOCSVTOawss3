package com.coderelated.dbTOCSVTOawss3.config;

import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.coderelated.dbTOCSVTOawss3.model.Users;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackageClasses = Users.class,
		entityManagerFactoryRef = "postgresEntityManagerFactory",
		transactionManagerRef = "postgresTransactionManager"
		)
public class PostgresJpaConfiguration {

	@Bean
	public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(
			@Qualifier("postgresDataSource") @Autowired(required = true) DataSource dataSource,
			EntityManagerFactoryBuilder entityManagerFactoryBuilder) {
		return entityManagerFactoryBuilder.dataSource(dataSource).packages(Users.class).build();
	}
	
	@Bean
	public PlatformTransactionManager postgresTransactionManager(@Qualifier("postgresEntityManagerFactory") 
	LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory) {
		return new JpaTransactionManager(Objects.requireNonNull(postgresEntityManagerFactory.getObject()));
		
	}
}
