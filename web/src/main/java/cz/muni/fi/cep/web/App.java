package cz.muni.fi.cep.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableAutoConfiguration
@Configuration
@EntityScan(basePackages = "cz.muni.fi.cep")
@ComponentScan(basePackages = "cz.muni.fi.cep")
public class App extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	
}
