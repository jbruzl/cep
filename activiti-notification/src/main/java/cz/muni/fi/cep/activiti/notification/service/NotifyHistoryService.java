package cz.muni.fi.cep.activiti.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

/**
 * Notify History Service
 * 
 * @author Jan Bruzl
 */
@Service("NotifyHistoryService")
@PropertySource("classpath:config/application-notify.properties")
public class NotifyHistoryService extends DefaultHistoryService {

	@Autowired
	public NotifyHistoryService(@Value("${cep.notify.key}")String key) {
		processDefinitionKey = key;
	}

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		return super.getDetail(pid);
	}

}
