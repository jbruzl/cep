/**
 * 
 */
package cz.muni.fi.bruzl.ces.serviceTasks;

import java.util.Properties;

import javax.annotation.Resource;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @author Jan Bruzl
 *
 */
public class InvokeSirenServiceTask implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String soundSirenUrl;

	/**
	 * @param soundSirenUrl the soundSirenUrl to set
	 */
	public void setSoundSirenUrl(String soundSirenUrl) {
		this.soundSirenUrl = soundSirenUrl;
	}

	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine.delegate.DelegateExecution)
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		RestTemplate restTemplate = new RestTemplate();		
		try {
			Boolean response = restTemplate.getForObject(soundSirenUrl, Boolean.class);
		}catch(ResourceAccessException e) {
			logger.error("Siren server {} refused connection. {}", soundSirenUrl, e.getMessage());
			throw new BpmnError("SirenRefusedConnection");
		}
	}
}