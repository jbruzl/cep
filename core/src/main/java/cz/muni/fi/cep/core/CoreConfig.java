package cz.muni.fi.cep.core;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jan Bruzl
 *
 */
@Configuration
@EnableAutoConfiguration
public class CoreConfig {
	
	@Bean
	public Mapper getMapper() {
		return new DozerBeanMapper();
	}
	
}
