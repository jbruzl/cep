package cz.muni.fi.cep.activiti.warning.chmi.service;

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
 * Inform Citizens History Service
 * 
 * @author Jan Bruzl
 */
@Service("WarningHistoryService")
@PropertySource("classpath:config/application-warning.properties")
public class WarningHistoryService extends DefaultHistoryService {

	@Autowired
	public WarningHistoryService(
			@Value("${cep.warning.key}") String key) {
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
			
			case "smsMessage":
				chvi.setVariableName("Upozornìní");
				filteredVariables.add(chvi);
				break;
			case "smsReceivers":
				chvi.setVariableName("Pøíjemci SMS");
				List<String> value = (List<String>) chvi.getValue();
				chvi.setValue(value);
				filteredVariables.add(chvi);
				break;
			case "emailInfoReceivers":
				chvi.setVariableName("Pøíjemci e-mailu");
				filteredVariables.add(chvi);
				break;
			case "decideInformCitizens":
				chvi.setVariableName("Informovat obyvatele?");
				if((Boolean)chvi.getValue())
					chvi.setValue("Ano");
				else
					chvi.setValue("Ne");
				filteredVariables.add(chvi);
				break;
			case "weatherChanged":
				chvi.setVariableName("Úroveò výstrah zmìnìna?");
				if((Boolean)chvi.getValue())
					chvi.setValue("Ano");
				else
					chvi.setValue("Ne");
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
