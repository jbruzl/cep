/**
 * 
 */
package cz.muni.fi.cep.warning.chmi.tasks;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.warning.chmi.report.Report;

/**
 * Service task that loads weather report and create its class representation.
 * 
 * @author Jan Bruzl
 *
 */
public class ObtainWeatherReport implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String sourceURL = "http://www.chmi.cz/files/portal/docs/meteo/om/zpravy/data/sivs_aktual.xml";

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
		try {
			RestTemplate restTemplate = new RestTemplate();
			report = restTemplate.getForObject(sourceURL, Report.class);
		} catch (Exception e) {
			logger.error("Error while obtaining weather report. {}", e.toString());
			throw new BpmnError("weatherReportLoadError");
		}

		execution.setVariable("weatherReport", report);
	}

}
