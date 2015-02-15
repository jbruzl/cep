package cz.muni.fi.cep.web.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.activiti.engine.form.FormProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.muni.fi.cep.activiti.notification.service.NotifyService;
import cz.muni.fi.cep.core.subscriptions.api.SubscriptionService;

/**
 * Web controller for Notify process. Later will be refactored to one controller
 * for all processes, depending on ProcessServiceManager.
 * 
 * @author Jan Bruzl
 *
 */
@Controller
@RequestMapping(value = { "/upozorneni" })
public class NotificationController {

	@Autowired
	private NotifyService notifyService;
	
	@Autowired
	private SubscriptionService subscriptionService;

	@RequestMapping(value = { "", "/" })
	public String displayStartForm(Model model) {
		Map<String, String> formData = new HashMap<>();
		for(FormProperty fp : notifyService.getStartForm().getFormProperties()) {
			formData.put(fp.getId(), null);
		}
		model.addAttribute("messgage", new String());
		return "notification/notification";
	}
	
	@RequestMapping(value = {"/start-process" })
	public String startProcess(Model model, String message) {
		HashMap<String, String> startParamMessage = new HashMap<>();
		startParamMessage.put("message", message);
		
		//notifyService.startTask("default", startParamMessage);
		
		return "redirect:/upozorneni";
	}

	@RequestMapping(value = { "/diagram" })
	@ResponseBody
	public ResponseEntity<byte[]> displayProcessDiagram() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.IMAGE_PNG);
		responseHeaders.set("Content-Disposition", "attachment; filename=diagram.png");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] imageInByte = null;
		try {
			ImageIO.write(notifyService.getDiagram(), "png", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<byte[]>(imageInByte, responseHeaders,
				HttpStatus.OK);

	}
	
	
}
