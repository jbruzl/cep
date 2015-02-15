package cz.muni.fi.cep.web.controllers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cz.muni.fi.cep.activiti.radio.service.BroadcastMessageService;

@Controller
@RequestMapping(value = { "/rozhlas" })
public class BroadcastController {
	
	private final String baseUploadPath="radioMessage/";
	
	@Autowired
	private BroadcastMessageService broadcastMessageService;
	
	@RequestMapping(value = { "", "/" })
	public String displayStartForm(Model model) {
		Map<String, String> formData = new HashMap<>();
		for(FormProperty fp : broadcastMessageService.getStartForm().getFormProperties()) {
			formData.put(fp.getId(), null);
		}
		model.addAttribute("messgage", new String());
		return "broadcast/broadcast";
	}
	
	@RequestMapping(value = {"/start-process" })
	public String startProcess(Model model, @RequestParam(value = "message", required = true) MultipartFile message) {
		String name = null;
		if (!message.isEmpty()) {
			try {
				byte[] bytes = message.getBytes();
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
				Date date = new Date();
				
				File folder = new File(baseUploadPath);
				if(!folder.exists()) {
					folder.mkdirs();
				}
				
				name = new StringBuilder().append(baseUploadPath).append(dateFormat.format(date)).append("-").append(message.getOriginalFilename()).toString();
				
				File file = new File(name);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(file));
				stream.write(bytes);
				stream.close();
				
			} catch (Exception e) {
				return "redirect:/rozhlas?pid=error";
			}
		} else {
			return "redirect:/rozhlas?pid=error";
		}
		
		ProcessInstance pi = null ;//= broadcastMessageService.startTask(name);
		
		if(pi==null) {
			//return "redirect:/rozhlas?pid=error";
		}
		
		return "redirect:/rozhlas?pid=";//+pi.getId();
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
			ImageIO.write(broadcastMessageService.getDiagram(), "png", baos);
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
