package cz.muni.fi.cep.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import cz.muni.fi.cep.core.users.services.IdentityService;
import cz.muni.fi.cep.core.users.services.UserService;

/**
 * @author Jan Bruzl
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages= {"cz.muni.fi.cep.core"})
@EntityScan(basePackages = { "cz.muni.fi.cep.core" })
public class App {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication
				.run(App.class);
		
		IdentityService is = context.getBean(IdentityService.class);
		
		//UserService service = context.getBean(UserService.class);
		
		/*User cepUser = new CepUserEntity();
		cepUser.setFirstName("Jan");
		cepUser.setLastName("Bruzl");
		cepUser.setEmail("bruzl.jan@gmail.com");
		//cepUser.setPhoneNumber("728484615");
		cepUser.setPassword("01234");
		
		
		
		is.saveUser(cepUser);
		
		for(User us : is.createUserQuery().list()) {
			System.out.println(us.toString());
			
		}*/
	}
}
