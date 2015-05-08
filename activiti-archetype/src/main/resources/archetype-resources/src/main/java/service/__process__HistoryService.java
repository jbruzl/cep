#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

/**
 * ${processName} History Service
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application-${process}.properties")
public class ${process}HistoryService extends DefaultHistoryService {

	@Autowired
	public ${process}HistoryService(@Value("${symbol_dollar}{cep.${process}.key}")String key) {
		processDefinitionKey = key;
	}

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		return super.getDetail(pid);
	}

}
