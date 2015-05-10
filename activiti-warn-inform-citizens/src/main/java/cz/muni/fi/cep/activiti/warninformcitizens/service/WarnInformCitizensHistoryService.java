package cz.muni.fi.cep.activiti.warninformcitizens.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.activiti.notification.messages.RadioMessage;
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

	@SuppressWarnings("unchecked")
	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		CepHistoryProcessInstance detail = super.getDetail(pid);
		List<CepHistoricVariableInstance> filteredVariables = new ArrayList<>();
		for (CepHistoricVariableInstance chvi : detail.getVariableInstances()) {
			switch (chvi.getVariableName()) {
			case "sendSMS":
				chvi.setVariableName("SMS?");
				filteredVariables.add(chvi);
				break;
			case "smsMessage":
				chvi.setVariableName("SMS");
				filteredVariables.add(chvi);
				break;				
			case "responded":
				chvi.setVariableName("Potvrzeno");
				List<String> checked = (List<String>) chvi.getValue();
				chvi.setValue(checked);
				filteredVariables.add(chvi);
				break;
			case "smsReceivers":
				chvi.setVariableName("Nepotvrzeno");
				List<String> value = (List<String>) chvi.getValue();
				chvi.setValue(value);
				filteredVariables.add(chvi);
				break;
			case "radioError":
				chvi.setVariableName("Chyba rozhlasu");
				filteredVariables.add(chvi);
				break;
			case "sirenError":
				chvi.setVariableName("Chyba sirény");
				filteredVariables.add(chvi);
				break;
			case "radioMessage":
				chvi.setVariableName("Rozhlasová zpráva");
				RadioMessage rm = (RadioMessage) chvi.getValue();
				chvi.setValue("<a href=\"/uploads/"
						+ rm.getRadioMessage().getFilename() + "\">Zpráva</a>");
				
				filteredVariables.add(chvi);
				break;
			case "checkResponse":
				chvi.setVariableName("Ověřovat odpověď?");
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
