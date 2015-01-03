package bpmn;

import static org.junit.Assert.*;

import javax.transaction.Transactional;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import cz.muni.fi.cep.core.App;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti.cfg.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
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
	

	@Test
	public void defaultTest() {
		assertNotNull(activitiRule);
		assertNotNull(historyService);
		assertNotNull(managementService);
		assertNotNull(runtimeService);
		assertNotNull(repositoryService);
	}
}
