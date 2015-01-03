package cz.muni.fi.cep.core.config;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
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
	
	@Bean
	public HistoryService getHistoryService(ProcessEngine processEngine) {
		return processEngine.getHistoryService();
	}
	
	@Bean
	public RuntimeService getRuntimeService(ProcessEngine processEngine) {
		return processEngine.getRuntimeService();
	}
	
	@Bean
	public ManagementService getManagementService(ProcessEngine processEngine) {
		return processEngine.getManagementService();
	}
	
	@Bean
	public RepositoryService getRepositoryService(ProcessEngine processEngine) {
		return processEngine.getRepositoryService();
	}
}
