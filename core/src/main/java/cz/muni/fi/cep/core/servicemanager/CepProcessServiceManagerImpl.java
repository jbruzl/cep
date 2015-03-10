package cz.muni.fi.cep.core.servicemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
	
	@Autowired
	private RepositoryService repositoryService;
	
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
		final User user = ((User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal());
				
		Set<String> groups = new HashSet<>();
		for(GrantedAuthority ga : user.getAuthorities()) {
			groups.add(ga.getAuthority());
		}
		
		List<CepProcessService> services = new ArrayList<>();
		
		for(ProcessDefinition pd : repositoryService.createProcessDefinitionQuery().list()) {
			ProcessDefinitionEntity pde  = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(pd.getId());
			for(Expression ex : pde.getCandidateStarterGroupIdExpressions()) {
				if(groups.contains(ex.getExpressionText())) {
					if(this.services.containsKey(pde.getKey()))
						services.add(this.services.get(pde.getKey()));
					break;
				}
			}
		}
		
		return services;
	}

}
