package cz.muni.fi.cep.web.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
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

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@Configuration
@PropertySource("classpath:config/application.properties")
public class Config {

	@Value("${cep.db.username}")
	private String username;
	
	@Value("${cep.db.password}")
	private String password;
	
	@Value("${cep.db.url}")
	private String url;
	
	@Value("${cep.db.port}")
	private String port;
	
	@Value("${cep.db.db}")
	private String dbName;
	
	@Bean
	public DataSource getDataSource() {
		 JdbcDataSource ds = new JdbcDataSource();
		 ds.setURL("jdbc:h2:˜/test");
		 ds.setUser("sa");
		 ds.setPassword("sa");
		
		MysqlDataSource d = new MysqlDataSource();
		d.setUser(username);
		d.setPassword(password);
		d.setCharacterEncoding("UTF-8");
		d.setDatabaseName(dbName);
		d.setServerName(url);
		d.setPortNumber(Integer.valueOf(port));

		return d;
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

	private Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		properties.setProperty("hibernate.dialect",
				"org.hibernate.dialect.MySQL5Dialect");
		properties.setProperty("hibernate.connection.characterEncoding",
				"UTF-8");
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
