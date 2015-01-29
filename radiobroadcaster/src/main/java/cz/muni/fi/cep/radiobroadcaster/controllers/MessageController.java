package cz.muni.fi.cep.radiobroadcaster.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cz.muni.fi.cep.radiobroadcaster.service.RadioMessageService;

@RestController
public class MessageController {
	@Autowired
	private RadioMessageService radioMessageService;
	
	private final String basePath = "";

	@RequestMapping("/broadcast")
	public String greeting(
			@RequestParam(value = "author", required = true) String author,
			@RequestParam(value = "message", required = true) MultipartFile message) {
		if (!message.isEmpty()) {
			try {
				byte[] bytes = message.getBytes();
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
				Date date = new Date();
				
				String name = new StringBuilder().append(basePath).append(dateFormat.format(date)).append(" ").append(message.getOriginalFilename()).toString();
				
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(name)));
				stream.write(bytes);
				stream.close();
				
				radioMessageService.addMessage(author, name);
				
				return "OK";
			} catch (Exception e) {
				return "NOK";
			}
		} else {
			return "NOK";
		}
	}
}
