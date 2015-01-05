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

import cz.muni.fi.cep.core.bpmn.service.api.MessageType;



/**
 * @author Jan Bruzl
 *
 */
public class SendSMSTask implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * @see org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine.delegate.DelegateExecution)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String message = (String) execution.getVariable("message");
		if(message == null) {			
			logger.error("Supplied unsupported variable: message. Quitting SendSMSTask.");
			return;
		}
		
		MessageType messageType = (MessageType) execution.getVariable("messageType");
		if(messageType == null) {
			logger.error("Supplied unsupported variable: messageType. Quitting SendSMSTask with params: {}", message);
			return;
		}
		
		List<String> receivers = null;
		if(execution.getVariable("receivers") instanceof List) {
				receivers = (List<String>) execution.getVariable("receivers");
		}else {
			logger.error("Supplied unsupported variable: receivers. Quitting SendSMSTask with params: {}, {}", message, messageType);
			return;
		}
		
		logger.info("Executing SendSMS task with params: {}, {}, {}", message, messageType, receivers);
		
		try {
			//TODO SMS logic
		}catch(Exception ex) {
			logger.error("Exception occured while sending SMS: {}", ex.toString());
			throw new BpmnError("SMSExceptionOccurred");
		}
		
	}

}
