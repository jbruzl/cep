package cz.muni.fi.cep.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jan Bruzl
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EntityScan(basePackages = { "cz.muni.fi.cep.core" })
public class App {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication
				.run(App.class);
		
		//UserService service = context.getBean(UserService.class);
		
		/*User cepUser = new CepUserEntity();
		cepUser.setFirstName("Jan");
		cepUser.setLastName("Bruzl");
		cepUser.setEmail("bruzl.jan@gmail.com");
		//cepUser.setPhoneNumber("728484615");
		cepUser.setPassword("01234");
		
		IdentityService is = context.getBean(IdentityService.class);
		
		is.saveUser(cepUser);
		
		for(User us : is.createUserQuery().list()) {
			System.out.println(us.toString());
			
		}*/
	}
}
