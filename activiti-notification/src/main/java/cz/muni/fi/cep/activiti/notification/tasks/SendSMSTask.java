package cz.muni.fi.cep.activiti.notification.tasks;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.BpmnError;
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
 * Implementation of Send SMS service task.
 * 
 * @author Jan Bruzl
 *
 */
@Component
public class SendSMSTask implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigurationManager configurationManager;

	private RestOperations restTemplate;
	
	public static final String loginKey = "cep.sms.login";
	public static final String passwordKey = "cep.sms.password";

	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public SendSMSTask() {
		restTemplate = new RestTemplate();
	}

	/**
	 * Sends SMS via smsbrana.cz
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ArrayList<String> receivers = null;

		if (execution.getVariable("smsReceivers") instanceof List) {
			receivers = (ArrayList<String>) execution
					.getVariable("smsReceivers");
		} else {
			logger.error("Supplied unsupported variable: receivers.");
			return;
		}

		ArrayList<String> unsendSMStrings = (ArrayList<String>) receivers
				.clone();

		String message = (String) execution.getVariable("smsMessage");
		if (message == null) {
			logger.error("Supplied unsupported variable: message. Quitting SendSMSTask.");
			execution.setVariable("unsucessfullSMS", unsendSMStrings);
			throw new BpmnError("smsSendError");
		}

		logger.info("Executing SendSMS task with params: {}, {}", message,
				receivers);
		final String encodedMessage = URLEncoder.encode(message, "UTF-8");

		try {
			logger.info("Starting to send sms");
			for (String smsReceiver : receivers) {
				
				String requestUrl = new StringBuilder()
						.append("http://api.smsbrana.cz/smsconnect/http.php")
						.append("?login=")
						.append(configurationManager.getKey(loginKey))
						.append("&password=")
						.append(configurationManager.getKey(passwordKey))
						.append("&action=send_sms").append("&number=")
						.append(smsReceiver).append("&message=")
						.append(encodedMessage).toString();
				URI uri = URI.create(requestUrl);
				String response = restTemplate.getForObject(uri, String.class);
				logger.info(response);
				unsendSMStrings.remove(smsReceiver);
			}

			logger.info("Sms sending ends");
		} catch (Exception ex) {
			logger.error("Exception occured while sending SMS: {}",
					ex.toString());
			execution.setVariable("unsucessfullSMS", unsendSMStrings);
			throw new BpmnError("smsSendError");
		}

	}
}
