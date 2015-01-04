/**
 * 
 */
package cz.muni.fi.bruzl.cep.main;

import org.activiti.engine.ProcessEngine;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.boot.SpringApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jan Bruzl
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("cz.muni.fi.bruzl.cep")
public class Application {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(Application.class);
		logger.info("Starting app");
		ApplicationContext context = SpringApplication.run(Application.class, args);
		ProcessEngine engine = context.getBean(ProcessEngine.class);
		System.out.println("init app context");
	}
}
