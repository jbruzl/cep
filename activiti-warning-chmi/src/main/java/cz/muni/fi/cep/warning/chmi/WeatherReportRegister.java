/**
 * 
 */
package cz.muni.fi.cep.warning.chmi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.warning.chmi.report.Report;

/**
 * @author Jan Bruzl
 *
 */
@Service
public class WeatherReportRegister implements Serializable {
	private static final long serialVersionUID = -8966006846535085676L;
	private final String saveFile = "reports.dat";
	private LinkedList<Report> reports = new LinkedList<>();
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public WeatherReportRegister() {
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		logger.info("Initializing WeatherReportRegister");
		Object obj = null;
		try {
			
			FileInputStream fis = new FileInputStream(saveFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
			ois.close();
		} catch (Exception ex) {
			logger.warn("Failed to load previous weather reports.");
		} 
		if (obj instanceof LinkedList<?>) {
			reports = (LinkedList<Report>) obj;
		}
		logger.info("Initializing of WeatherReportRegister finished");
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		FileOutputStream fos = new FileOutputStream(saveFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(reports);

		fos.close();
	}

	public LinkedList<Report> getReports() {
		return reports;
	}

	public void setReports(LinkedList<Report> reports) {
		this.reports = reports;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
