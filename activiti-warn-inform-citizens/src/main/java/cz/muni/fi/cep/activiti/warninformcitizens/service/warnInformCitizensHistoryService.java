package cz.muni.fi.cep.activiti.warninformcitizens.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

/**
 * Warn and inform citizens History Service
 * 
 * @author Jan Bruzl
 */
//@Service
@PropertySource("classpath:config/application-warnInformCItizens.properties")
public class warnInformCitizensHistoryService extends DefaultHistoryService {

	@Autowired
	public warnInformCitizensHistoryService(@Value("${cep.warnInformCItizens.key}")String key) {
		processDefinitionKey = key;
	}

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		return super.getDetail(pid);
	}

}
