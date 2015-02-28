package cz.muni.fi.cep.api.services.configurationmanager;

import java.io.Serializable;
import java.util.Set;

/**
 * Interface of configuration manager component. Component provides central
 * singleton service for storage of settings and of values needed for
 * application run. Provides service similar to Spring environment, where values
 * could be easily added/registered and modified.
 * 
 * @author Jan Bruzl
 *
 */
public interface ConfigurationManager extends Serializable {

	/**
	 * Returns set of registered keys.
	 * 
	 * @return {@link Set}
	 */
	public Set<String> getKeySet();

	/**
	 * Returns value of given key. Null if key is not registered.
	 * @param key
	 * @return value of key
	 */
	public String getKey(String key);

	/**
	 * Registers given key with given value. If key is registered, value will be updated.
	 * @param key
	 * @param new value of key
	 */
	public void setKey(String key, String value);
}
