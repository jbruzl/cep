package cz.muni.fi.cep.core.configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ConfigurationManager}
 * 
 * @author Jan Bruzl
 */
@Component("configurationManager")
public class ConfigurationManagerImpl implements ConfigurationManager {
	private static final long serialVersionUID = 7735116354010333835L;
	private Map<String, String> values = new HashMap<>();
	private String fileName;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Loads serialized values
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		logger.info("Initializing ConfigurationManager");
		Object obj = null;
		try {

			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
			ois.close();
		} catch (Exception ex) {
			logger.warn("Failed to load previous weather reports.");
		}
		if (obj instanceof HashMap<?, ?>) {
			values = (HashMap<String, String>) obj;
		}
		logger.info("Initializing of ConfigurationManager finished");
	}

	/**
	 * Save serialized values
	 */
	@PreDestroy
	public void destroy() {
		//TODO refine
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(values);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public Set<String> getKeySet() {
		return Collections.unmodifiableSet(values.keySet());
	}

	@Override
	public String getKey(String key) {
		if (values.containsKey(key)) {
			return values.get(key);
		}
		return null;
	}

	@Override
	public void setKey(String key, String value) {
		values.put(key, value);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
