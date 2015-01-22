/**
 * 
 */
package cz.muni.fi.cep.core.bpmn.servicetask;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @author Jan Bruzl
 *
 */
public class InvokeSirenServiceTask implements JavaDelegate {
	/*
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String soundSirenUrl = "http://localhost:9000/soundSiren";
*/
	/**
	 * @param soundSirenUrl the soundSirenUrl to set
	 */
	public void setSoundSirenUrl(String soundSirenUrl) {
		//this.soundSirenUrl = soundSirenUrl;
	}

	/* (non-Javadoc)
	 * @see org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine.delegate.DelegateExecution)
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		/*
		RestTemplate restTemplate = new RestTemplate();		
		try {
			restTemplate.getForObject(soundSirenUrl, Boolean.class);
		}catch(ResourceAccessException e) {
			logger.error("Siren server {} refused connection. {}", soundSirenUrl, e.getMessage());
			throw new BpmnError("SirenRefusedConnection");
		}
		*/
	}
}
