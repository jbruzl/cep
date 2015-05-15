/**
 * 
 */
package cz.muni.fi.cep.activiti.warning.chmi.tasks;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.activiti.warning.chmi.report.Report;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;

/**
 * Service task that loads weather report and create its class representation.
 * 
 * @author Jan Bruzl
 *
 */
@Component
public class ObtainWeatherReport implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ConfigurationManager configurationManager;

	private RestOperations restTemplate;
	
	public final static String chmiUrlKey = "cep.warning.chmi.url";

	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public ObtainWeatherReport() {
		restTemplate = new RestTemplate();
	}

	/**
	 * Tries to obtain weather report and parse it to class structure. Then pass
	 * it as process variable.
	 * 
	 * @see org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine.delegate.DelegateExecution)
	 * @throws {@link BpmnError}
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {

		Report report = null;
		String sourceURL = configurationManager.getKey(chmiUrlKey);

		try {
			report = restTemplate.getForObject(sourceURL, Report.class);
		} catch (Exception e) {
			logger.error("Error while obtaining weather report. {}", e.toString());
			throw new BpmnError("weatherReportLoadError");
		}

		execution.setVariable("weatherReport", report);
	}

}
