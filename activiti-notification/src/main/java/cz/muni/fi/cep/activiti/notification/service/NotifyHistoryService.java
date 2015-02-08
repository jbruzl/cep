package cz.muni.fi.cep.activiti.notification.service;

import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.CepHistoryProcessInstance;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

/**
 * Notify History Service
 * 
 * @author Jan Bruzl
 */
@Service("NotifyHistoryService")
public class NotifyHistoryService extends DefaultHistoryService {

	public NotifyHistoryService() {
		processDefinitionKey = "Notify";
	}

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return super.getDetail(pid);
	}

}
