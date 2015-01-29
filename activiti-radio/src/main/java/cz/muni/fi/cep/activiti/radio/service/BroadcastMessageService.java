package cz.muni.fi.cep.activiti.radio.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.FormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.activiti.radio.messages.FileRadioMessage;
import cz.muni.fi.cep.core.subscriptions.api.SubscriptionService;

/**
 * Service class for BPMN diagram.
 * 
 * All interaction with  process should be done thru this service.
 * 
 * @author Jan Bruzl
 */
@Service
public class BroadcastMessageService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private FormService formService;

	@Autowired
	private HistoryService historicService;

	@Autowired
	private SubscriptionService subscriptionService;

	private ProcessDefinition processDefinition;

	private final String processDefinitionKey = "broadcastmessage";

	@PostConstruct
	public void init() {
		logger.info("Initialising Broadcast Message service");

		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey).singleResult() == null)
			repositoryService.createDeployment()
					.addClasspathResource("diagrams/BroadcastMessage.bpmn").deploy();
		logger.debug("Process Broadcast Message deployed");

		processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey).singleResult();
		if (processDefinition != null)
			logger.debug("Process definition obtained");
		else
			logger.warn("Process definition could not be obtained");

		logger.info("Broadcast Message service initialised");
	}

	public void unregisterService() {
	}


	public ProcessInstance startTask(String messageFile) {
		logger.info("Checking service state");
		if (processDefinition == null) {
			logger.warn("Process definition not loaded, trying now.");
			processDefinition = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionKey(processDefinitionKey).singleResult();
			if (processDefinition == null) {
				logger.error("Could not load process definition.");
				return null;
			}
		}
		logger.info("Service state OK");

		File f = new File(messageFile);
		if(!f.exists())
			return null;
		
		FileRadioMessage frm = new FileRadioMessage();
		frm.setAudioFileName(f.getPath());
		frm.setAuthor("");
		frm.setRecordDate(GregorianCalendar.getInstance());
		
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("radioMessage", frm);


		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				processDefinition.getKey(), params);
		logger.info("Process Notify started");
		return pi;
	}

	public FormData getStartForm() {
		logger.info("Checking service state");
		if (processDefinition == null) {
			logger.warn("Process definition not loaded, trying now.");
			processDefinition = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionKey(processDefinitionKey).singleResult();
			if (processDefinition == null) {
				logger.error("Could not load process definition.");
				return null;
			}
		}
		logger.info("Service state OK");

		FormData formData = formService.getStartFormData(processDefinition
				.getId());
		logger.info("Returning process Broadcast Message start form data");
		return formData;
	}

	/**
	 * Returns percentage progress of process instance with given id. Percentage
	 * is computed as ratio of completed tasks to overall tasks.
	 * 
	 * TODO move to history service
	 * 
	 * @param processInstanceId
	 * @return Float within range 0 - 1
	 */
	public Float getProcessInstanceProgress(String processInstanceId) {
		List<HistoricActivityInstance> activitiList = getProcessActivitiHistory(processInstanceId);

		Float progress = new Float(0);
		for (HistoricActivityInstance hai : activitiList) {
			if (hai.getEndTime() != null)
				progress += 1;
		}
		if (progress != 0.0) {
			progress = progress / activitiList.size();
		}

		logger.info(
				"Returning progress {} % of instance {} of process Broadcast Message.",
				progress * 100, processInstanceId);
		return progress;
	}

	/**
	 * Returns list of {@link HistoricActivityInstance} of process instance with
	 * given id.
	 * 
	 * TODO move to history service
	 * 
	 * @param processInstanceId
	 * @return List of {@link HistoricActivityInstance}
	 */
	public List<HistoricActivityInstance> getProcessActivitiHistory(
			String processInstanceId) {
		return historicService.createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.processInstanceId(processInstanceId).list();
	}

	/**
	 * Gives access to a deployed process diagram, e.g., a PNG image, through a
	 * stream of bytes.
	 *
	 * @return {@link BufferedImage}
	 * @throws ActivitiObjectNotFoundException
	 *             when the process diagram doesn't exist.
	 */
	public BufferedImage getProcessDiagram() {
		logger.info("Checking service state");
		if (processDefinition == null) {
			logger.warn("Process definition not loaded, trying now.");
			processDefinition = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionKey(processDefinitionKey).singleResult();
			if (processDefinition == null) {
				logger.error("Could not load process definition.");
				return null;
			}
		}
		logger.info("Service state OK");

		ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
		logger.info("Generating PNG image of process Notify");
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition
				.getId());
		logger.info("Returning generated PNG image of process Broadcast Message");
		return diagramGenerator.generatePngImage(bpmnModel, 1);
	}

}
