/**
 * 
 */
package cz.muni.fi.cep.core.servicemanager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.muni.fi.cep.api.DTO.forms.CepFormData;
import cz.muni.fi.cep.api.DTO.forms.CepFormProperty;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.servicemanager.CepHistoryService;
import cz.muni.fi.cep.api.services.servicemanager.CepProcessService;
import cz.muni.fi.cep.api.services.servicemanager.CepProcessServiceManager;
import cz.muni.fi.cep.api.services.users.IdentityService;

/**
 * Abstract super class of all {@link CepProcessService} implementations.
 * 
 * @author Jan Bruzl
 */
public abstract class AbstractCepProcessService implements CepProcessService {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected CepProcessServiceManager processServiceManager;
	
	@Autowired
	protected RepositoryService repositoryService;
	@Autowired
	protected RuntimeService runtimeService;
	@Autowired
	protected FormService formService;
	@Autowired
	protected HistoryService historicService;
	@Autowired
	protected TaskService taskService;
	@Autowired
	protected ConfigurationManager configurationManager;
	@Autowired
	protected IdentityService identityService;
	@Autowired(required=true)
	protected Mapper mapper;
	
	protected CepHistoryService cepHistoryService;
	
	protected ProcessDefinition processDefinition;
	protected String processKey;
	protected String processName;
	protected String key;
	protected String name;
	protected String description;

	
	
	@Override
	public CepHistoryService getHistoryService() {
		return cepHistoryService;
	}

	@Override
	public BufferedImage getDiagram() {
		ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
		logger.info("Generating PNG image of process {}", getProcessKey());
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition
				.getId());
		logger.info("Returning generated PNG image of process {}",
				getProcessKey());
		return diagramGenerator.generatePngImage(bpmnModel, 1);
	}

	@Override
	public BufferedImage getDiagram(String pid)
			throws IllegalArgumentException {
		ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
		logger.info("Generating PNG image of process {}", getProcessKey());
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition
				.getId());

		List<String> activities = new ArrayList<>();

		for (HistoricActivityInstance hai : historicService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(pid).finished().list()) {
			activities.add(hai.getActivityId());
		}

		logger.info("Returning generated PNG image of process {}",
				getProcessKey());
		try {
			return ImageIO.read(diagramGenerator.generateDiagram(bpmnModel,
					"PNG", activities));
		} catch (IOException e) {
			logger.error("Error while generating png image. {}", e.getMessage());
			return null;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getProcessKey() {
		return processKey;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public CepFormData getStartForm() {
		FormData startData = formService.getStartFormData(processDefinition.getId());

		CepFormData cepStartForm = mapper.map(startData, CepFormData.class);
		for(FormProperty fp: startData.getFormProperties()) {
			CepFormProperty cfp = mapper.map(fp, CepFormProperty.class);
			cepStartForm.getFormProperties().add(cfp);
		}


		return cepStartForm;
	}

	@Override
	public List<Task> getTasks(ProcessInstance pid) {
		return taskService.createTaskQuery().processInstanceId(pid.getId())
				.list();
	}

}