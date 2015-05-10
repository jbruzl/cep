package cz.muni.fi.cep.activiti.staffcall.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.history.CepHistoricVariableInstance;
import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

/**
 * Emergency Staff Call History Service
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

	@SuppressWarnings("unchecked")
	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		CepHistoryProcessInstance detail = super.getDetail(pid);
		List<CepHistoricVariableInstance> filteredVariables = new ArrayList<>();
		for (CepHistoricVariableInstance chvi : detail.getVariableInstances()) {
			switch (chvi.getVariableName()) {
			case "smsReceivers":
				chvi.setVariableName("Nepotvrzená čísla");				
				List<String> unresponded = (List<String>) chvi.getValue();
				chvi.setValue(unresponded);
				filteredVariables.add(chvi);
				break;
			case "responded":
				chvi.setVariableName("Potvrzená čísla");
				List<String> responded = (List<String>) chvi.getValue();
				chvi.setValue(responded);
				filteredVariables.add(chvi);
				break;
			case "smsMessage":
				chvi.setVariableName("Zpráva");
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
