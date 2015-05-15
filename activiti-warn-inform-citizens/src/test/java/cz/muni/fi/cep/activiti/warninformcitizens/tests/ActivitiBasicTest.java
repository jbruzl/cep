package cz.muni.fi.cep.activiti.warninformcitizens.tests;


import static org.junit.Assert.assertNotNull;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Activiti test supertype
 * 
 * @author Jan Bruzl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti.cfg.xml")
@TransactionConfiguration(defaultRollback=true)
@DirtiesContext
public class ActivitiBasicTest{
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();
	
	@Autowired
	protected HistoryService historyService;
	@Autowired
	protected RuntimeService runtimeService;
	@Autowired
	protected ManagementService managementService;
	@Autowired
	protected RepositoryService repositoryService;
	@Autowired
	protected FormService formService;
	@Autowired
	protected TaskService taskService;

	@Test
	public void defaultTest() {
		assertNotNull(activitiRule);
		assertNotNull(historyService);
		assertNotNull(managementService);
		assertNotNull(runtimeService);
		assertNotNull(repositoryService);
		assertNotNull(formService);
		assertNotNull(taskService);
	}
}