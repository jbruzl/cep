/**
 * 
 */
package cz.muni.fi.cep.warning.chmi.tasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.muni.fi.cep.warning.chmi.WeatherReportRegister;
import cz.muni.fi.cep.warning.chmi.report.Report;

/**
 * Service task that evaluates obtained weather report and saves it to register
 * for later user.
 * 
 * Implemented as {@link Component} because of needs for @Autowired service.
 * 
 * @author Jan Bruzl
 */
@Component
public class EvaluateWarningReport implements JavaDelegate {
	@Autowired
	private WeatherReportRegister weatherReportRegistr;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public EvaluateWarningReport() {
	}

	/**
	 * 
	 * @see org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine.delegate.DelegateExecution)
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Report report = (Report) execution.getVariable("weatherReport");

		if (report == null) {
			logger.error("Report is null. It souldn't be null, errors within obtaining report should be catched earlier.");
			return;
		}
		
		//TODO evaluate

		weatherReportRegistr.getReports().add(report);

		execution.setVariable("weatherChanged", true);
		execution.setVariable("warningLevelRisen", false);
	}

}
