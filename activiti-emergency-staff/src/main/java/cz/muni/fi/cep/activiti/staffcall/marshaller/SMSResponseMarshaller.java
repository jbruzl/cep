/**
 * 
 */
package cz.muni.fi.cep.activiti.staffcall.marshaller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * SMS Response marshaller
 * 
 * @author Jan Bruzl
 */
@Configuration
public class SMSResponseMarshaller {

	@Bean
	public Jaxb2Marshaller getSMSResponseMarshaller() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setPackagesToScan("cz.muni.fi.cep.activiti.staffcall.smsresponse");
		
		return jaxb2Marshaller;
	}
}
