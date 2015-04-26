/**
 * 
 */
package cz.muni.fi.cep.api.services.servicemanager;

import java.util.List;

import org.activiti.engine.task.Task;

/**
 * Interface for manager of process services.
 * 
 * Registers process services, information about them.
 * 
 * @author Jan Bruzl
 */
public interface CepProcessServiceManager {

	/**
	 * Registers given service.
	 * 
	 * @param service {@link CepProcessService}
	 */
	public void registerService(CepProcessService service);
	
	/**
	 * Unregisters given service.
	 * 
	 * @param service {@link CepProcessService}
	 */
	public void unregisterService(CepProcessService service);
	
	/**
	 * Returns process service with given key. Null if name is invalid.
	 * 
	 * @param key {@link String}
	 * @return {@link CepProcessService}
	 */
	public CepProcessService getServiceByKey(String key);
	
	/**
	 * Returns process service with given name. Null if name is invalid.
	 * 
	 * @param name {@link String}
	 * @return {@link CepProcessService}
	 */
	public CepProcessService getServiceByName(String name);
	
	/**
	 * Returns list of all process services
	 * @return {@link List} of {@link CepProcessService}
	 */
	public List<CepProcessService> getServices();
	
	/**
	 * Returns list of all process services that current user can run
	 * 
	 * @return {@link List} of {@link CepProcessService}
	 */
	public List<CepProcessService> getAvailableServices();

	/**
	 * Returns list of all {@link Task}s available for logged user.
	 * 
	 * @return {@link List} of {@link Task}
	 */
	public abstract List<Task> getAvailableTasks();
}
