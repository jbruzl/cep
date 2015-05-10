package cz.muni.fi.cep.web.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController implements ErrorController {
	private static final String PATH = "/error";
	
	@Value("${cep.web.upload.folder}")
	private String uploadFolder;
	
	@RequestMapping(value = { "/index", "/", "" })
	public String index() {
		return "index";
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}
	
	@RequestMapping("/error")
	public String error(HttpServletRequest request, Model model) {
		model.addAttribute("errorCode",
				request.getAttribute("javax.servlet.error.status_code"));
		Throwable throwable = (Throwable) request
				.getAttribute("javax.servlet.error.exception");
		String errorMessage = null;
		if (throwable != null) {
			errorMessage = throwable.getMessage();
		}
		model.addAttribute("errorMessage", errorMessage);
		return "error";
	}
	
	@RequestMapping("/uploads/{file:.+}")
	public ResponseEntity<byte[]> uploads(HttpServletRequest request, Model model, @PathVariable("file") String file) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Disposition",
				"attachment; filename="+file);
		
		byte[] fileInByte = null;
		try {
			File f = new File(uploadFolder+file);
			if(!f.exists()){
				return new ResponseEntity<byte[]>(fileInByte, responseHeaders,
						HttpStatus.BAD_REQUEST);
			}
			fileInByte = Files.readAllBytes(f.toPath());
			
		} catch (IOException e) {
			return null;
		}
		return new ResponseEntity<byte[]>(fileInByte, responseHeaders,
				HttpStatus.OK);
	}
	
}
