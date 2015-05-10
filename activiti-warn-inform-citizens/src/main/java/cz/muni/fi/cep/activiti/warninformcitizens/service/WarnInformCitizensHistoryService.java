package cz.muni.fi.cep.activiti.warninformcitizens.service;

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
 * Warn and inform citizens History Service
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application-warnInformCitizens.properties")
public class WarnInformCitizensHistoryService extends DefaultHistoryService {

	@Autowired
	public WarnInformCitizensHistoryService(@Value("${cep.warnInformCitizens.key}")String key) {
		processDefinitionKey = key;
	}

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		CepHistoryProcessInstance detail = super.getDetail(pid);
		List<CepHistoricVariableInstance> filteredVariables = new ArrayList<>();
		for (CepHistoricVariableInstance chvi : detail.getVariableInstances()) {
			switch (chvi.getVariableName()) {/*
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
				
			case "radioMessage":
				chvi.setVariableName("Zpráva");
				RadioMessage rm = (RadioMessage) chvi.getValue();
				chvi.setValue("<a href'" + rm.getRadioMessage().getPath() + "'>Zpráva</a>");
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
				break;*/
			default:
				break;
			}
		}
		detail.setVariableInstances(filteredVariables);

		return detail;
	}

}
