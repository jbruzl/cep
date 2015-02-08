package cz.muni.fi.cep.api.services;

import java.util.List;

import org.activiti.engine.HistoryService;

import cz.muni.fi.cep.api.DTO.CepHistoryProcessInstance;

/**
 * Interface for local facade of {@link HistoryService}.
 * 
 * @author Jan Bruzl
 */
public interface CepHistoryService {
	
	/**
	 * Returns list of all process instances.
	 * 
	 * @return List of {@link CepHistoryProcessInstance}
	 */
	public List<CepHistoryProcessInstance> getAllInstances();
	
	/**
	 * Returns list of finished process instances.
	 * 
	 * @return List of {@link CepHistoryProcessInstance}
	 */
	public List<CepHistoryProcessInstance> getFinishedInstances();
	
	/**
	 * Returns list of unfinished process instances.
	 * 
	 * @return List of {@link CepHistoryProcessInstance}
	 */
	public List<CepHistoryProcessInstance> getRunningInstances();
	
	/**
	 * Returns fully loaded {@link CepHistoryProcessInstance} with all details.
	 * @param pid Process instance id
	 * @return {@link CepHistoryProcessInstance} or null if process instance doesn't exists
	 * @throws IllegalArgumentException if process with pid is not supported
	 */
	public CepHistoryProcessInstance getDetail(String pid) throws IllegalArgumentException ;
}
