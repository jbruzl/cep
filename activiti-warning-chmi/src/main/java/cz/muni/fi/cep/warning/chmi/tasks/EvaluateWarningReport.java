/**
 * 
 */
package cz.muni.fi.cep.warning.chmi.tasks;

import java.util.LinkedList;

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

	public void setWeatherReportRegistr(WeatherReportRegister weatherReportRegistr) {
		this.weatherReportRegistr = weatherReportRegistr;
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
			execution.setVariable("weatherChanged", false);
			execution.setVariable("warningLevelRisen", false);
			
			return;
		}
		
		LinkedList<Report> reports = weatherReportRegistr.getReports();
		if(reports.isEmpty()) {
			execution.setVariable("weatherChanged", true);
			execution.setVariable("warningLevelRisen", true);
			
			return;
		}
		
		Report prevReport = reports.getLast();
		reports.add(report);
		
		

		boolean warningChange = false;
		boolean warningLevelRisen = false;
		
		//TODO evaluate
		/*String countriesOfInterestCode = "CZ";
		List<String> countriesOfInterestCodeList = Arrays.asList(countriesOfInterestCode.split(","));
		Map<String, Country> countriesCurrent = new HashMap<>();
		Map<String, Country> countriesOld = new HashMap<>();
		
		for(Country c : prevReport.getCountries()) {
			if(countriesOfInterestCodeList.contains(c.getCode()))
				countriesOld.put(c.getCode(), c);
		}
		
		for(Country c : report.getCountries()) {
			if(countriesOfInterestCodeList.contains(c.getCode())) {
				
				countriesCurrent.put(c.getCode(), c);
			}
				
		}
		
*/
		execution.setVariable("weatherChanged", warningChange);
		execution.setVariable("warningLevelRisen", warningLevelRisen);
	}

}
