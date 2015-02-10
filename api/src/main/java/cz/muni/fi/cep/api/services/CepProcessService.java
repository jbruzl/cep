/**
 * 
 */
package cz.muni.fi.cep.api.services;

import java.awt.image.BufferedImage;

import org.activiti.engine.runtime.ProcessInstance;

import cz.muni.fi.cep.api.DTO.CepFormData;

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
	 * Returnd {@link BufferedImage} of process with highlighted state of given {@link ProcessInstance}.
	 * 
	 * @param pid {@link ProcessInstance}
	 * @return {@link BufferedImage}
	 * @throws IllegalArgumentException if given pid is not of services process
	 */
	public BufferedImage getDiagram(ProcessInstance pid) throws IllegalArgumentException;
	
	/**
	 * Returns name of process.
	 * 
	 * @return {@link String}
	 */
	public String getName();
	
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
	
	//TODO interactions with running process
}
