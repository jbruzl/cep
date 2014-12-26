/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import cz.muni.fi.bruzl.cep.subscriptions.api.ContactType;
import cz.muni.fi.bruzl.cep.subscriptions.api.SubscriptionService;
import cz.muni.fi.bruzl.cep.subscriptions.dao.PublisherDao;
import cz.muni.fi.bruzl.cep.subscriptions.dao.SubscriberDao;

/**
 * @author Jan Bruzl
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EntityScan(basePackages = { "cz.muni.fi.bruzl.cep.subscriptions.entities",
		"cz.muni.fi.cep.core.entities" })
public class App {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication
				.run(App.class);
		context.getBean(PublisherDao.class);
		context.getBean(SubscriberDao.class);
		System.out.println("Its alive!");

		SubscriptionService service = context
				.getBean(SubscriptionService.class);

		service.register("a");

		service.subscribe("hello", "a", ContactType.EMAIL);
		service.unsubscribe("hello", "a", null);

	}
}
