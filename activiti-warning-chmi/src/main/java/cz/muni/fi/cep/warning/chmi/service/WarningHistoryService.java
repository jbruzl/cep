package cz.muni.fi.cep.warning.chmi.service;

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

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		CepHistoryProcessInstance detail = super.getDetail(pid);
		List<CepHistoricVariableInstance> filteredVariables = new ArrayList<>();
		for (CepHistoricVariableInstance chvi : detail.getVariableInstances()) {
			switch (chvi.getVariableName()) {
			/*
			case "sendSMS":
				chvi.setVariableName("SMS?");
				filteredVariables.add(chvi);
				break;
			case "sendRadio":
				chvi.setVariableName("Rozhlas?");
				filteredVariables.add(chvi);
				break;
			case "sendEmail":
				chvi.setVariableName("Email?");
				filteredVariables.add(chvi);
				break;
			case "smsMessage":
				chvi.setVariableName("SMS");
				filteredVariables.add(chvi);
				break;
			case "emailMessage":
				chvi.setVariableName("Email");
				filteredVariables.add(chvi);
				break;
			case "sendEmailError":
				if (chvi.getValue() != null) {
					chvi.setVariableName("Chyba při odesílání emailu");
					filteredVariables.add(chvi);
				}
				break;
			case "smsReceivers":
				chvi.setVariableName("Příjemci SMS");
				List<String> value = (List<String>) chvi.getValue();
				chvi.setValue(value);
				filteredVariables.add(chvi);
				break;
			case "mailList":
				chvi.setVariableName("Příjemci e-mailu");
				filteredVariables.add(chvi);
				break;
				*/
			default:
				break;
			}
		}
		detail.setVariableInstances(filteredVariables);

		return detail;
	}

}
