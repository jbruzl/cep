/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import cz.muni.fi.bruzl.cep.subscriptions.dao.PublisherDao;
import cz.muni.fi.bruzl.cep.subscriptions.dao.SubscriberDao;
import cz.muni.fi.bruzl.cep.subscriptions.entities.ContactType;
import cz.muni.fi.bruzl.cep.subscriptions.entities.OrphanSubscriber;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Publisher;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Subscriber;
import cz.muni.fi.bruzl.cep.subscriptions.entities.UserSubscriber;
import cz.muni.fi.cep.core.entities.CepUser;


/**
 * @author Jan Bruzl
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class App {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(App.class);
		PublisherDao publisherDao = context.getBean(PublisherDao.class);
		SubscriberDao subscriberDao = context.getBean(SubscriberDao.class);
		System.out.println("Its alive!");
		
		Publisher publisher = new Publisher();
		publisher.setCode("0041");
		
		publisherDao.save(publisher);
		
		Iterable<Publisher> publishers = publisherDao.findAll();
		System.out.println(publishers);
		
		Subscriber subscriber = new OrphanSubscriber("bruzl.jan@gmail.com");
		subscriber.setContactType(ContactType.EMAIL);
		
		subscriberDao.save(subscriber);
		
		System.out.println(subscriberDao.findAll());
		
		CepUser user = new CepUser();
		user.setFirstName("Jan");
		user.setLastName("Bruzl");
		user.setEmail("bruzl.jan@gmail.com");
		user.setPassword("#");
		user.setPhoneNumber("0123456789");
		Subscriber subscriber2 = new UserSubscriber(user);
		subscriber2.setContactType(ContactType.SMS);
		subscriberDao.save(subscriber2);
		
		System.out.println(subscriberDao.findAll());
	}
}
