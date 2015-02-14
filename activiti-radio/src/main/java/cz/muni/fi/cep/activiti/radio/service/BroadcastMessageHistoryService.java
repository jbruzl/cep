package cz.muni.fi.cep.activiti.radio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.CepHistoryProcessInstance;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

/**
 * Broadcast Message History Service
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application.properties")
public class BroadcastMessageHistoryService extends DefaultHistoryService {

	@Autowired
	public BroadcastMessageHistoryService(@Value("${cep.radio.key}")String key) {
		processDefinitionKey = key;
	}

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		return super.getDetail(pid);
	}

}
