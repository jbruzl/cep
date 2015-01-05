package cz.muni.fi.cep.web.config;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivitiConfig {
	@Bean
	public ProcessEngine getProcessEngine() {
		return ProcessEngines.getDefaultProcessEngine();
	}
	
	@Bean
	public RuntimeService getRuntimeService(ProcessEngine processEngine) {
		return processEngine.getRuntimeService();
	}
	
	@Bean
	public HistoryService getHistoryService(ProcessEngine processEngine) {
		return processEngine.getHistoryService();
	}
	
	@Bean
	public FormService getFormService(ProcessEngine processEngine) {
		return processEngine.getFormService();
	}
	
	@Bean
	public TaskService getTaskService(ProcessEngine processEngine) {
		return processEngine.getTaskService();
	}
	
	@Bean
	public RepositoryService getrRepositoryService(ProcessEngine processEngine) {
		return processEngine.getRepositoryService();
	}
}
