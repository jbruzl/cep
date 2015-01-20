package cz.muni.fi.cep.activiti.notification.service;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import cz.muni.fi.cep.core.bpmn.service.api.MessageType;
import cz.muni.fi.cep.core.subscriptions.api.SubscriptionService;

/**
 * Service class for BPMN diagram Notify.
 * 
 * All interaction with Notify process should be done thru this service.
 * 
 * @author Jan Bruzl
 */
@Service
public class NotifyService {
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

	private static final String publisherCode = "Notify";

	private ProcessDefinition processDefinition;

	public NotifyService() {

	}

	@PostConstruct
	public void init() {
		logger.info("Initialising Notify service");

		subscriptionService.register(publisherCode);
		logger.debug("Publisher {} registered", publisherCode);

		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("Notify").singleResult() == null)
			repositoryService.createDeployment()
					.addClasspathResource("diagrams/SendSMS.bpmn")
					.addClasspathResource("diagrams/Notify.bpmn").deploy();
		logger.debug("Process Notify deployed");
		logger.debug("Process SendSMS deployed");

		processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("Notify").singleResult();
		if (processDefinition != null)
			logger.debug("Process definition obtained");
		else
			logger.warn("Process definition could not be obtained");

		logger.info("Notify service initialised");
	}

	public void unregisterService() {
		//subscriptionService.unregister(publisherCode);
	}

	public static String getPublisherCode() {
		return publisherCode;
	}

	public ProcessInstance startTask(String templateKey,
			Map<String, String> message) {
		logger.info("Checking service state");
		if (processDefinition == null) {
			logger.warn("Process definition not loaded, trying now.");
			processDefinition = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionKey("Notify").singleResult();
			if (processDefinition == null) {
				logger.error("Could not load process definition.");
				return null;
			}
		}
		logger.info("Service state OK");

		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);
		params.put("templateKey", templateKey);
		params.put("templateVariable", message);
		params.put("notificationEnabled", "true"); // TODO remove later when
													// config manager is
													// implemented in process
		params.put("messageType", MessageType.NOTIFICATION);

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
					.processDefinitionKey("Notify").singleResult();
			if (processDefinition == null) {
				logger.error("Could not load process definition.");
				return null;
			}
		}
		logger.info("Service state OK");

		FormData formData = formService.getStartFormData(processDefinition
				.getId());
		logger.info("Returning process Notify start form data");
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
				"Returning progress {} % of instance {} of process Notify.",
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
					.processDefinitionKey("Notify").singleResult();
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
		logger.info("Returning generated PNG image of process Notify");
		return diagramGenerator.generatePngImage(bpmnModel, 1);
	}

}
