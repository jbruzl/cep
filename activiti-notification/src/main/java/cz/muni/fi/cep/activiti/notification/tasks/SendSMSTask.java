/**
 * 
 */
package cz.muni.fi.cep.activiti.notification.tasks;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;

import cz.muni.fi.cep.core.bpmn.service.api.MessageType;

/**
 * @author Jan Bruzl
 *
 */
public class SendSMSTask implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());

	// twilio credentials
	// TODO move to property file
	private static final String ACCOUNT_SID = "ACabbccc26315f0e1dd04fbd3802ca10cf";
	private static final String AUTH_TOKEN = "a50c88393dc38156842334da611d99a9";

	// twilio sender number
	// TODO move to property file
	private static final String SENDER = "+12012796704";

	/**
	 * Sends SMS via twilio provider.
	 * 
	 * @see org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine.delegate.DelegateExecution)
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
			TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID,
					AUTH_TOKEN);
			MessageFactory messageFactory = client.getAccount()
					.getMessageFactory();

			for (String smsReceiver : receivers) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("Body", message));
				params.add(new BasicNameValuePair("From", SENDER));
				params.add(new BasicNameValuePair("To", smsReceiver));
				try {
					messageFactory.create(params);
				} catch (TwilioRestException ex) {
					logger.error("Could not send sms to {}, exception: {}",
							smsReceiver, ex.toString());
				}
			}
			logger.info("Sms sending ends");
		} catch (Exception ex) {
			logger.error("Exception occured while sending SMS: {}",
					ex.toString());
			throw new BpmnError("SMSExceptionOccurred");
		}

	}

}
