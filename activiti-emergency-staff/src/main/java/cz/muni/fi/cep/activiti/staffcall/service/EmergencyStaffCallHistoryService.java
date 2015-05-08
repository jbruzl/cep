package cz.muni.fi.cep.activiti.staffcall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

/**
 * Example History Service
 * 
 * @author Jan Bruzl
 */
@Service("ExampleHistoryService")
@PropertySource("classpath:config/application-emergencyStaffCall.properties")
public class EmergencyStaffCallHistoryService extends DefaultHistoryService {

	@Autowired
	public EmergencyStaffCallHistoryService(@Value("${cep.emergencyStaffCall.key}")String key) {
		processDefinitionKey = key;
	}

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		return super.getDetail(pid);
	}

}
