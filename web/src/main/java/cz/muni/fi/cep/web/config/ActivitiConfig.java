package cz.muni.fi.cep.web.config;

import javax.sql.DataSource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ActivitiConfig {
	@Bean
    public ProcessEngine processEngine(ProcessEngineConfigurationImpl pec, ApplicationContext applicationContext) throws Exception {
        ProcessEngineFactoryBean pe = new ProcessEngineFactoryBean();
        pe.setProcessEngineConfiguration(pec);
        pe.setApplicationContext(applicationContext);

        return pe.getObject();
    }

    @Bean
    public ProcessEngineConfigurationImpl getProcessEngineConfiguration(
            DataSource dataSource,
            PlatformTransactionManager transactionManager,
            ApplicationContext context) {
        SpringProcessEngineConfiguration pec = new SpringProcessEngineConfiguration();

        pec.setDataSource(dataSource);
        pec.setDatabaseSchemaUpdate("true");
        pec.setJobExecutorActivate(true);
        pec.setHistory("full");
        
        pec.setDatabaseType("mysql");
        
        //Mail settings
        pec.setMailServerPort(587);
        pec.setMailServerHost("smtp.gmail.com");
        pec.setMailServerUseTLS(true);
        pec.setMailServerUseSSL(true);
        pec.setMailServerDefaultFrom("bruzl.jan@gmail.com");
        pec.setMailServerUsername("bruzl.jan@gmail.com");
        pec.setMailServerPassword("Adic321&");

        pec.setTransactionManager(transactionManager);
        pec.setApplicationContext(context);

        return pec;
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
