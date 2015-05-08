/**
 * 
 */
package cz.muni.fi.cep.activiti.warninformcitizens.tasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;

/**
 * @author Jan Bruzl
 *
 */
@Component
public class SirenTask implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private RestOperations restTemplate;
	
	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public SirenTask() {
		restTemplate = new RestTemplate();
	}

	@Autowired
	private ConfigurationManager configurationManager;
	
	private final String broadcastUrlKey = "cep.radio.broadcast.url";

	/**
	 * Broadcast Task of BroadcastMessage diagram. Obtains execution variable
	 * "radioMessage" and then broadcast it via remote broadcast service (only
	 * stub).
	 * 
	 * {@see
	 * org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine.
	 * delegate.DelegateExecution)}
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {

	}
}
