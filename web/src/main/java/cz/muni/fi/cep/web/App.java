package cz.muni.fi.cep.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@EnableAutoConfiguration
@Configuration
@EntityScan(basePackages = "cz.muni.fi.cep")
@ComponentScan(basePackages = "cz.muni.fi.cep")
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
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
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine ste = new SpringTemplateEngine();
		ste.setTemplateResolver(templateResolver());
		//ste.setMessageSource(messageSource());
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
