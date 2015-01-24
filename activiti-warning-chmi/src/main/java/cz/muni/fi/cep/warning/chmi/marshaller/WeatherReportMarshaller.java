/**
 * 
 */
package cz.muni.fi.cep.warning.chmi.marshaller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * @author Jan Bruzl
 *
 */
@Configuration
public class WeatherReportMarshaller {

	@Bean
	public Jaxb2Marshaller getWeatherReportMarshaller() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setPackagesToScan("cz.muni.fi.cep.warning.chmi.report");
		
		return jaxb2Marshaller;
	}
}
