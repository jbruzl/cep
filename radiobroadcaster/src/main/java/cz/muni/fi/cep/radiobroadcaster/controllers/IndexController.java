package cz.muni.fi.cep.radiobroadcaster.controllers;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.muni.fi.cep.radiobroadcaster.data.RadioMessage;
import cz.muni.fi.cep.radiobroadcaster.service.RadioMessageService;

@Controller
public class IndexController {
	@Autowired
	private RadioMessageService rms;
	
	@ModelAttribute("messages")
	public List<RadioMessage> getMessages(){
		return rms.getMessages();
	}
	
	@RequestMapping(value= {"/","/index"})
	public String index() {
		
		return "index";
	}
	
	@RequestMapping("/getMessage")
	@ResponseBody
	public ResponseEntity<byte[]> getMessage(@RequestParam(value="message", required=true) String message) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Disposition", "attachment; filename=message.wav");
		responseHeaders.set("content-type", "audio/x-wav");
		RadioMessage rm = rms.getMessage(message);
		byte[] bytes;
		try {
			bytes = IOUtils.toByteArray(rm.getRadioMessage().getInputStream());
		} catch (IOException e) {
			bytes = null;
			e.printStackTrace();
		}
		return new ResponseEntity<byte[]>(bytes, responseHeaders,
				HttpStatus.OK);
	}
}
