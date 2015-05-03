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

import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.warning.chmi.WeatherReportRegister;
import cz.muni.fi.cep.warning.chmi.report.Country;
import cz.muni.fi.cep.warning.chmi.report.Region;
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
	
	@Autowired
	private ConfigurationManager configurationManager;
	
	final static public String countryCodeKey = "cep.warning.countryCode";
	final static public String regionCodeKey = "cep.warning.regionCode";

	public void setWeatherReportRegistr(
			WeatherReportRegister weatherReportRegistr) {
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
		
		if (reports.isEmpty()) {
			execution.setVariable("weatherChanged", true);
			execution.setVariable("warningLevelRisen", true);
			reports.add(report);
			return;
		}
		
		Report prevReport = reports.getLast();
		reports.add(report);

		
		String countryCode = configurationManager.getKey(countryCodeKey);
		String regionCode = configurationManager.getKey(regionCodeKey);

		Country prevCountry = null, currentCountry = null;

		for (Country c : prevReport.getCountries()) {
			if (countryCode.equals(c.getCode()))
				prevCountry = c;
		}

		for (Country c : report.getCountries()) {
			if (countryCode.equals(c.getCode()))
				currentCountry = c;
		}

		// given country not found
		if (currentCountry == null) {
			execution.setVariable("weatherChanged", false);
			execution.setVariable("warningLevelRisen", false);

			return;
		}

		// current awareness level code not equal to previous, obtain regions
		Region prevRegion = null, currentRegion = null;

		if (prevCountry != null)
			for (Region r : prevCountry.getRegions()) {
				if (regionCode.equals(r.getCode())) {
					prevRegion = r;
				}
			}

		for (Region r : currentCountry.getRegions()) {
			if (regionCode.equals(r.getCode())) {
				currentRegion = r;
			}
		}

		// region not found
		if (currentRegion == null) {
			execution.setVariable("weatherChanged", false);
			execution.setVariable("warningLevelRisen", false);

			return;
		}

		// previous region found or awareness level code 0 used
		Integer prevRegionAwarenessLevelCode = 0;
		if (prevRegion != null)
			prevRegionAwarenessLevelCode = Integer.decode(prevRegion
					.getAwarenessLevelCode());
		
		Integer currentRegionAwarenessLevelCode = 0;
		currentRegionAwarenessLevelCode = Integer.decode(currentRegion.getAwarenessLevelCode());
		
		if(prevRegionAwarenessLevelCode == currentRegionAwarenessLevelCode) {
			execution.setVariable("weatherChanged", false);
			execution.setVariable("warningLevelRisen", false);

			return;
		}
		
		execution.setVariable("weatherChanged", true);
		
		if(prevRegionAwarenessLevelCode < currentRegionAwarenessLevelCode) {
			execution.setVariable("warningLevelRisen", true);
		}else {
			execution.setVariable("warningLevelRisen", false);
		}
	}

}
