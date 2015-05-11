package cz.muni.fi.cep.radiobroadcaster.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.radiobroadcaster.data.RadioMessage;

@Service
@EnableScheduling
public class RadioMessageService {
	private Map<String, RadioMessage> messages = new HashMap<>();

	public List<RadioMessage> getMessages() {
		return new ArrayList<RadioMessage>(messages.values());
	}

	@PreDestroy
	public void cleanUp() {
		for(RadioMessage rm : messages.values()){
			rm.getRadioMessage().getFile().delete();
		}
	}
	
	@PostConstruct
	public void init() {
		if(messages==null)
			messages = new HashMap<>();
	}

	public void addMessage(String author, String file) {
		Calendar cal = GregorianCalendar.getInstance();
		
		RadioMessage rm = new RadioMessage();
		rm.setAuthor(author);
		rm.setRecordDate(cal);
		
		File f = new File(file);
		if(f.exists()) {
			rm.setAudioFileName(f.getPath());
			messages.put(f.getName(), rm);
		}
	}
	
	@Scheduled(cron="0 0 0/1 1/1 * ?")
	public void cleanMessages() {
		cleanUp();
		init();
	}
	
	public RadioMessage getMessage(String filename) {
		return messages.get(filename);
	}
	
}
