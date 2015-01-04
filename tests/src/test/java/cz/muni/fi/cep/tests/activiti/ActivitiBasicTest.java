package cz.muni.fi.cep.tests.activiti;

import static org.junit.Assert.*;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import cz.muni.fi.cep.tests.BasicTest;

public class ActivitiBasicTest extends BasicTest{
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

	@Test
	public void defaultTest() {
		assertNotNull(activitiRule);
		assertNotNull(historyService);
		assertNotNull(managementService);
		assertNotNull(runtimeService);
		assertNotNull(repositoryService);
		assertNotNull(formService);
	}
}