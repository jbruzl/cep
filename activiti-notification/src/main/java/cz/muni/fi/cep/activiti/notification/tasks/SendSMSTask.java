/**
 * 
 */
package cz.muni.fi.cep.activiti.notification.tasks;

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
import cz.muni.fi.cep.core.bpmn.service.api.MessageType;

/**
 * @author Jan Bruzl
 *
 */
@Component
public class SendSMSTask implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigurationManager configurationManager;

	private RestOperations restTemplate;

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
		String message = (String) execution.getVariable("message");
		if (message == null) {
			logger.error("Supplied unsupported variable: message. Quitting SendSMSTask.");
			return;
		}

		MessageType messageType = (MessageType) execution
				.getVariable("messageType");
		if (messageType == null) {
			logger.error(
					"Supplied unsupported variable: messageType. Quitting SendSMSTask with params: {}",
					message);
			return;
		}

		List<String> receivers = null;
		if (execution.getVariable("receivers") instanceof List) {
			receivers = (List<String>) execution.getVariable("receivers");
		} else {
			logger.error(
					"Supplied unsupported variable: receivers. Quitting SendSMSTask with params: {}, {}",
					message, messageType);
			return;
		}

		logger.info("Executing SendSMS task with params: {}, {}, {}", message,
				messageType, receivers);

		try {
			logger.info("Starting to send sms");
			for (String smsReceiver : receivers) {

				String response = restTemplate
						.getForObject(
								"http://api.smsbrana.cz/smsconnect/http.php?login={login}&password={heslo}&action=send_sms&number={number}&message={message}",
								String.class, configurationManager
										.getKey("cep.notify.sms.login"),
								configurationManager
										.getKey("cep.notify.sms.password"),
								smsReceiver, message);
				logger.info(response);

			}

			logger.info("Sms sending ends");
		} catch (Exception ex) {
			logger.error("Exception occured while sending SMS: {}",
					ex.toString());
			throw new BpmnError("SMSExceptionOccurred");
		}

	}
}
