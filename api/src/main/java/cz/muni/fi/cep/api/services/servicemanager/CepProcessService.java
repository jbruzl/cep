/**
 * 
 */
package cz.muni.fi.cep.api.services.servicemanager;

import java.awt.image.BufferedImage;
import java.util.List;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import cz.muni.fi.cep.api.form.CepFormData;

/**
 * Interface of service for process.
 * 
 * Recommended use: One run-able process one service implementation. In case of
 * multiple start task or with multiple start configuration use one
 * implementation for each case.
 * 
 * @author Jan Bruzl
 *
 */
public interface CepProcessService {
	/**
	 * Starts process with given {@link CepFormData}.
	 * 
	 * @param data {@link CepFormData}
	 * @return {@link ProcessInstance}
	 */
	public ProcessInstance runProcess(CepFormData data);
	
	/**
	 * Return {@link BufferedImage} of process.
	 * 
	 * @return {@link BufferedImage}
	 */
	public BufferedImage getDiagram();
	
	/**
	 * Return {@link BufferedImage} of process with highlighted state of given {@link ProcessInstance}.
	 * 
	 * @param pid {@link String} Process Instance ID
	 * @return {@link BufferedImage}
	 * @throws IllegalArgumentException if given pid is not of services process
	 */
	public BufferedImage getDiagram(String pid) throws IllegalArgumentException;
	
	/**
	 * Returns name of process.
	 * 
	 * @return {@link String}
	 */
	public String getName();
	
	/**
	 * Returns key of start point.
	 * 
	 * Defined as processKey:id
	 * 
	 * @return {@link String}
	 */
	public String getKey();
	
	/**
	 * Returns key of process.
	 * 
	 * @return {@link String}
	 */
	public String getProcessKey();
	
	/**
	 * Returns description of process.
	 * 
	 * @return {@link String}
	 */
	public String getDescription();
	
	/**
	 * Returns {@link CepFormData} needed to run process. 
	 * 
	 * @return {@link CepFormData}
	 */
	public CepFormData getStartForm();
	
	/**
	 * Returns list of unfinished {@link Task}s of given pid.
	 * 
	 * @param pid {@link ProcessInstance}
	 * @return {@link List} of {@link Task}
	 */
	public List<Task> getTasks(ProcessInstance pid);
	
	/**
	 * Given task is is completed with given {@link CepFormData}
	 * @param taskId {@link String}
	 * @param data {@link CepFormData}
	 * @return processInstanceId
	 */
	public String complete(String taskId, CepFormData data);
	
	/**
	 * Returns {@link CepHistoryService} associated with service.
	 * @return {@link CepHistoryService}
	 */
	public CepHistoryService getHistoryService();

	/**
	 * Returns {@link CepFormData} needed for given task id. 
	 * @param taskId
	 * @return {@link CepFormData} or null if taskId is not valid
	 */
	public abstract CepFormData getTaskForm(String taskId);

	/**
	 * Returns list of unfinished {@link Task}s of services process.
	 * @return {@link List} of {@link Task}
	 */
	public abstract List<Task> getAvailableTasks();
	
	/**
	 * Returns list of unfinished {@link Task}s of given process instance.
	 * @param processInstanceId
	 * @return {@link List} of {@link Task}
	 */
	public abstract List<Task> getAvailableTasks(String processInstanceId);
}
