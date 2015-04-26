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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.activiti.radio.service.BroadcastMessageHistoryService;
import cz.muni.fi.cep.activiti.radio.service.BroadcastMessageService;
import cz.muni.fi.cep.activiti.radio.tasks.BroadcastTask;
import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;

public class BroadcastServiceTest extends ActivitiBasicTest {
	private String testAudioFile = "src/test/resources/test.wav";

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private BroadcastTask broadcastTask;

	private final String broadcastUrlKey = "cep.radio.broadcast.url";

	@Autowired
	private BroadcastMessageService service;

	@Autowired
	private BroadcastMessageHistoryService broadcastMessageHistoryService;

	@Test
	public void testStart() {
		configurationManager.setKey(broadcastUrlKey,
				"http://localhost:8080/broadcast");

		RestTemplate restTemplate = new RestTemplate();
		MockRestServiceServer mockServer = MockRestServiceServer
				.createServer(restTemplate);
		mockServer
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		broadcastTask.setRestTemplate(restTemplate);

		FormData startForm = service.getStartForm();
		assertNotNull("Start form data souldn't be null", startForm);
		assertTrue("Start form data should be instance of CepFormData",
				startForm instanceof CepFormData);

		FormProperty messageFP = null;
		for (FormProperty fp : startForm.getFormProperties()) {
			if (fp.getId().equals("radioMessage"))
				messageFP = fp;
		}
		assertNotNull("Message file not found in form property", messageFP);

		assertTrue("Form property should be instance of CepFormProperty",
				messageFP instanceof CepFormProperty);

		((CepFormProperty) messageFP).setInput(testAudioFile);

		ProcessInstance pi = service.runProcess((CepFormData) startForm);
		assertNotNull("Process instance is null", pi);

		CepHistoryProcessInstance chpi = broadcastMessageHistoryService
				.getDetail(pi.getId());
		assertNotNull(
				"Historic process instance is null, process does not have recorded progress",
				chpi);
	}

	@Before
	public void login() {
		List<GrantedAuthority> gaList = new ArrayList<>();
        User user = new User("test", "test",
                true, true, true, true, gaList);

	    SecurityContextHolder.getContext().setAuthentication(
	        new UsernamePasswordAuthenticationToken(user, "test")
	    );
	}

	@After
	public void logout() {
		SecurityContextHolder.clearContext();
	}

}
