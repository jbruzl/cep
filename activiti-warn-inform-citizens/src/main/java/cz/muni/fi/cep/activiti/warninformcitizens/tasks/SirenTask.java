/**
 * 
 */
package cz.muni.fi.cep.activiti.warninformcitizens.tasks;

import java.util.Calendar;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.activiti.notification.messages.FileRadioMessage;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;

/**
 * @author Jan Bruzl
 *
 */
@Component
public class SirenTask implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private RestOperations restTemplate;
	private String audioFileName = "src/test/resources/test.wav";
	
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
		FileRadioMessage rm = new FileRadioMessage();
		rm.setAuthor("system");
		rm.setRecordDate(Calendar.getInstance());
		rm.setAudioFileName(audioFileName);
		
		String broadcastUrl = configurationManager.getKey(broadcastUrlKey);
		if (broadcastUrl == null) {
			logger.error("Can't continue broadcast message, couldn't obtain service url.");
			throw new BpmnError("sendRadioMessageError");
		}

		FileSystemResource is = rm.getRadioMessage();
		if (is == null) {
			logger.error(
					"Can't continue broadcast message {}, couldn't obtain audio message.",
					rm);
			throw new BpmnError("sendRadioMessageError");
		}
		
		try {
			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
			parts.add("message", is);
			parts.add("author", rm.getAuthor());

			String response = restTemplate.postForObject(broadcastUrl, parts, String.class);
			logger.info("Response: {}", response);
		}catch(RestClientException ex) {
			logger.error("Couldn't contact broadcast service. {}", ex.toString());
			throw new BpmnError("sendRadioMessageError");
		} 
	}
}
