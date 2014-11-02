/**
 * 
 */
package cz.muni.fi.bruzl.ces.serviceTasks;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jan Bruzl
 *
 */
public class InvokeRadioRelay implements JavaDelegate {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String radioUrl;
	

	/**
	 * @param radioUrl the radioUrl to set
	 */
	public void setRadioUrl(String radioUrl) {
		this.radioUrl = radioUrl;
	}


	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine.delegate.DelegateExecution)
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String relaySoundPath = (String) execution.getVariable("relaySoundPath");
		if(relaySoundPath==null) {
			logger.error("Invalid resource given: Incorrect relay sound path. Given: {}", relaySoundPath);
			throw new BpmnError("RadioError");
		}
		
		FileSystemResource fsr = new FileSystemResource(relaySoundPath);
		if(!fsr.exists()) {
			logger.error("Invalid resource given: Incorrect relay sound path. Given: {}", relaySoundPath);
			throw new BpmnError("RadioError");
		}
		
		RestTemplate restTemplate = new RestTemplate();				
		try {	
			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();			
			parts.add("file", fsr);
			restTemplate.postForObject(radioUrl, parts, Boolean.class);
		}catch(ResourceAccessException e) {
			logger.error("Radio server {} refused connection. {}", radioUrl, e.getMessage());
			throw new BpmnError("RadioError");
		}

	}

}
