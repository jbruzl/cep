package cz.muni.fi.cep.activiti.staffcall.tasks;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.activiti.staffcall.smsresponse.SMSItem;
import cz.muni.fi.cep.activiti.staffcall.smsresponse.SMSResult;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;

/**
 * @author Jan Bruzl
 *
 */
@Component
public class CheckSMSResponses implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigurationManager configurationManager;

	private RestOperations restTemplate;

	public static final String loginKey = "cep.sms.login";
	public static final String passwordKey = "cep.sms.password";

	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public CheckSMSResponses() {
		restTemplate = new RestTemplate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Integer retryCount = (Integer) execution.getVariable("retryCount");
		if (retryCount == null) {
			retryCount = 1;
		} else {
			retryCount++;
		}

		ArrayList<String> receivers = null;

		if (execution.getVariable("smsReceivers") instanceof List) {
			receivers = (ArrayList<String>) execution
					.getVariable("smsReceivers");
		} else {
			logger.error("Supplied unsupported variable: receivers.");
			return;
		}

		SMSResult smsResult = null;

		String requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey(loginKey))
				.append("&password=")
				.append(configurationManager.getKey(passwordKey))
				.append("&action=inbox").append("&delete=1")
				.toString();
		URI uri = URI.create(requestUrl);
		smsResult = restTemplate.getForObject(uri, SMSResult.class);

		ArrayList<String> responded = new ArrayList<>();
		ArrayList<String> unresponded = new ArrayList<>(receivers);

		if (smsResult != null && smsResult.getSmsInbox() != null
				&& smsResult.getSmsInbox().getSmsDelivery() != null) {
			for (SMSItem item : smsResult.getSmsInbox().getSmsDelivery()
					.getItems()) {
				if (unresponded.contains(item.getNumber())) {
					responded.add(item.getNumber());
					unresponded.remove(item.getNumber());
				}
			}
		}
		execution.setVariable("retryCount", retryCount);
		execution.setVariable("responded", responded);
		execution.setVariable("smsReceivers", unresponded);
	}
}
