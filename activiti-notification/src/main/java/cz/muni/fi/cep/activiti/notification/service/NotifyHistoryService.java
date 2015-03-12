package cz.muni.fi.cep.activiti.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.history.CepHistoricVariableInstance;
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
		CepHistoryProcessInstance detail = super.getDetail(pid);
		List<CepHistoricVariableInstance> filteredVariables = new ArrayList<>();
		for(CepHistoricVariableInstance chvi : detail.getVariableInstances()) {
			switch(chvi.getVariableName()) {
				case "initiator":
					chvi.setVariableName("Spustil");
					filteredVariables.add(chvi);
					break;
				case "publisherCode":
					chvi.setVariableName("Událost");
					filteredVariables.add(chvi);
					break;
				case "notificationEnabled":
					chvi.setVariableName("Notifikace povolena?");					
					filteredVariables.add(chvi);
					break;
				case "templateVariable":
					chvi.setVariableName("Zpráva");
					Map<String, String> vars =(HashMap<String, String>)chvi.getValue();
					chvi.setValue(vars.get("message"));
					filteredVariables.add(chvi);
					break;
				case "receivers":
					chvi.setVariableName("Příjemci SMS");
					List<String> value = (List<String>)chvi.getValue();
					chvi.setValue(value);
					filteredVariables.add(chvi);
					break;
				case "mailList":
					chvi.setVariableName("Příjemci e-mailu");
					filteredVariables.add(chvi);
					break;
				default:
					break;
			}
		}
		detail.setVariableInstances(filteredVariables);
		
		return detail;
	}

}
