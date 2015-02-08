package cz.muni.fi.cep.activiti.radio.service;

import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.CepHistoryProcessInstance;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

/**
 * Broadcast Message History Service
 * 
 * @author Jan Bruzl
 */
@Service("BroadcastMessageHistoryService")
public class BroadcastMessageHistoryService extends DefaultHistoryService {

	public BroadcastMessageHistoryService() {
		processDefinitionKey = "broadcastmessage";
	}

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return super.getDetail(pid);
	}

}
