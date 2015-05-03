package cz.muni.fi.cep.web.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
@PropertySource("classpath:config/application.properties")
public class Config {

	@Value("${cep.db.username}")
	private String username;
	
	@Value("${cep.db.password}")
	private String password;
	
	@Value("${cep.db.url}")
	private String url;
	
	@Bean
	public DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);


		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(getDataSource());
		

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean
	public PlatformTransactionManager transactionManager(
			EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		properties.setProperty("hibernate.dialect",
				"org.hibernate.dialect.MySQL5Dialect");
		properties.setProperty("hibernate.connection.characterEncoding",
				"utf8");
		properties.setProperty("hibernate.connection.useUnicode",
				"true");
		properties.setProperty("hibernate.connection.charSet",
				"UTF-8");
		
		return properties;
	}
	
	@Bean
	public TemplateResolver templateResolver() {
		TemplateResolver tempRes = new SpringResourceTemplateResolver();

		tempRes.setCharacterEncoding("UTF-8");
		tempRes.setTemplateMode("HTML5");
		tempRes.setSuffix(".html");
		tempRes.setPrefix("classpath:/templates/");
		tempRes.setCacheable(false);
		return tempRes;
	}

	@Bean
    public SpringSecurityDialect thymeleafSecurityDialect(){
        return new SpringSecurityDialect();
    } 
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine ste = new SpringTemplateEngine();
        ste.setTemplateResolver(templateResolver());
        ste.addDialect(thymeleafSecurityDialect());
        return ste;
    }

	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver tvr = new ThymeleafViewResolver();

		tvr.setCharacterEncoding("UTF-8");
		tvr.setTemplateEngine(templateEngine());

		return tvr;
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		return filter;
	}
}
