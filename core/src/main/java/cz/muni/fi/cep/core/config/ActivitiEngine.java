package cz.muni.fi.cep.core.config;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivitiEngine {
	@Bean
	public ProcessEngine getProcessEngine() {
		return ProcessEngines.getDefaultProcessEngine();
	}
	
	/*
	@Bean
	public IdentityService getIdentityService(ProcessEngine processEngine) {
		return processEngine.getIdentityService();
	}*/
}
