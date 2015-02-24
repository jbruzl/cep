#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import ${package}.service.${processName}HistoryService;
import ${package}.service.${processName}Service;
import ${groupId}.api.DTO.CepHistoryProcessInstance;
import ${groupId}.api.DTO.forms.CepFormData;
import ${groupId}.core.configuration.ConfigurationManager;
import ${groupId}.core.users.api.IdentityService;

public class ${processName}ServiceTest extends ActivitiBasicTest  {
	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private ${processName}Service service;

	@Autowired
	private ${processName}HistoryService historyService;
	

	@Test
	public void testStart() {
		FormData startForm = service.getStartForm();
		assertNotNull("Start form data souldn't be null", startForm);
		assertTrue("Start form data should be instance of CepFormData",
				startForm instanceof CepFormData);

		for (FormProperty fp : startForm.getFormProperties()) {
			//fill in start form
		}


		ProcessInstance pi = service.runProcess((CepFormData) startForm);
		assertNotNull("Process instance is null", pi);
		

		CepHistoryProcessInstance chpi = historyService
				.getDetail(pi.getId());
		assertNotNull(
				"Historic process instance is null, process does not have recorded progress",
				chpi);
	}
	
	@Before
	public void setUp() {
		assertNotNull("Identity service null", identityService);
		
		List<GrantedAuthority> gaList = new ArrayList<>();
        User user = new User("test", "test",
                true, true, true, true, gaList);

	    SecurityContextHolder.getContext().setAuthentication(
	        new UsernamePasswordAuthenticationToken(user, "test")
	    );
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
