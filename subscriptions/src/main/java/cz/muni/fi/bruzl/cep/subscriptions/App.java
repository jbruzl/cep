/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import cz.muni.fi.bruzl.cep.subscriptions.dao.PublisherDao;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Publisher;

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
		System.out.println("Its alive!");
		
		Publisher publisher = new Publisher();
		publisher.setCode("0041");
		
		publisherDao.save(publisher);
		
		Iterable<Publisher> publishers = publisherDao.findAll();
	}
}
