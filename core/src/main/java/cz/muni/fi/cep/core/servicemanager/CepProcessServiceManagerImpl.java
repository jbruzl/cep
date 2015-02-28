package cz.muni.fi.cep.core.servicemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.services.servicemanager.CepProcessService;
import cz.muni.fi.cep.api.services.servicemanager.CepProcessServiceManager;

/**
 * Implementation of {@link CepProcessServiceManager}
 * 
 * @author Jan Bruzl
 */
@Service("processServiceManager")
public class CepProcessServiceManagerImpl implements CepProcessServiceManager {
	/**
	 * Map of services, key is process start point id.
	 */
	private Map<String, CepProcessService> services = new HashMap<>();
	
	/**
	 * Maps start point names to start point id.
	 */
	private Map<String, String> serviceNames = new HashMap<>();
	
	@Override
	public void registerService(CepProcessService service) {
		if(service == null)
			throw new IllegalArgumentException("Given service in null");
		
		services.put(service.getKey(), service);
		serviceNames.put(service.getName(), service.getKey());
	}

	@Override
	public void unregisterService(CepProcessService service) {
		if(service == null)
			throw new IllegalArgumentException("Given service in null");
		
		String key = service.getKey();
		if(services.containsKey(key))
				services.remove(key);
		String name = service.getName();
		if(serviceNames.containsKey(name))
			services.remove(name);
		
	}

	@Override
	public CepProcessService getServiceByKey(String key) {
		if(key == null)
			throw new IllegalArgumentException("Given null key");
		return services.get(key);
	}

	@Override
	public CepProcessService getServiceByName(String name) {
		if(name == null)
			throw new IllegalArgumentException("Given null name");
		String key = serviceNames.get(name);
		if(key == null)
			return null;
		return services.get(key);
	}

	@Override
	public List<CepProcessService> getServices() {
		return new ArrayList<CepProcessService>(services.values());
	}

	@Override
	public List<CepProcessService> getAvaibleServices() {
		//TODO implement when access namagement is implemented 
		return getServices();
	}

}
