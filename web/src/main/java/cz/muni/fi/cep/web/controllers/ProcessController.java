/**
 * 
 */
package cz.muni.fi.cep.web.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cz.muni.fi.cep.api.DTO.forms.CepFileFormType;
import cz.muni.fi.cep.api.DTO.forms.CepFormData;
import cz.muni.fi.cep.api.DTO.forms.CepFormProperty;
import cz.muni.fi.cep.api.services.CepProcessService;
import cz.muni.fi.cep.api.services.CepProcessServiceManager;

/**
 * Controller responsible for processes
 * 
 * @author Jan Bruzl
 */
@Controller
@RequestMapping(value = { "/proces" })
public class ProcessController {

	@Autowired
	private CepProcessServiceManager processServiceManager;

	@Value("${cep.web.upload.folder}")
	private String uploadFolder;

	@RequestMapping(value = "/start/{key}")
	public String startForm(@PathVariable("key") String key, Model model) {
		CepProcessService service = processServiceManager.getServiceByKey(key);
		if (service == null) {
			return "process/error?proces=" + key + "&state=notfound";
		}

		CepFormData startForm = service.getStartForm();
		model.addAttribute("startForm", startForm);

		model.addAttribute("process", service.getName());
		model.addAttribute("description", service.getDescription());

		return "process/start";
	}

	@RequestMapping(value = "/start/submit/{key}", method = RequestMethod.POST)
	public String startProcess(@PathVariable("key") String key, Model model,
			HttpServletRequest httpServletRequest) {
		CepProcessService service = processServiceManager.getServiceByKey(key);
		if (service == null) {
			return "process/error?proces=" + key + "&state=notfound";
		}

		CepFormData startForm = service.getStartForm();

		for (FormProperty fp : startForm.getFormProperties()) {
			if (!(fp instanceof CepFormProperty))
				continue;
			CepFormProperty cfp = (CepFormProperty) fp;

			if (cfp.getType() instanceof CepFileFormType) {
				// process file upload
				MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;
				MultipartFile mf = multipartHttpServletRequest.getFile(cfp
						.getId());

				File f = new File(uploadFolder + mf.getOriginalFilename());
				try {
					InputStream is = mf.getInputStream();
					OutputStream os = new FileOutputStream(f);
					byte[] buffer = new byte[8 * 1024];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					System.out.println(is.available());
					is.close();
					os.close();

				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					return "process/error?proces=" + key + "&state=uploaderror";
				}
				cfp.setInput(f.getAbsolutePath());
			} else {
				String parameter = httpServletRequest.getParameter(cfp.getId());
				cfp.setInput(parameter);
			}
		}
		ProcessInstance pi = service.runProcess(startForm);
		if (pi == null) {
			return "redirect:/proces/error?proces=" + key + "&state=startfail";
		}
		return "redirect:/historie/instance/" + key + "/" + pi.getId();
	}

	@RequestMapping("/error")
	public String error(
			@RequestParam(value = "proces", required = true) String key,
			@RequestParam(value = "state") String state, Model model) {
		CepProcessService service = processServiceManager.getServiceByKey(key);
		if (service == null) {
			model.addAttribute("process", "Neznámý proces");
		} else {
			model.addAttribute("process", service.getName());
		}

		switch (state) {
		case "uploaderror":
			model.addAttribute("description", "Vyskytla se chyba během nahrání souboru. Zkuste to prosím znova.");
			break;
		case "startfail":
			model.addAttribute("description", "Nepodařilo se spustit proces. Zkuste to prosím znova.");
			break;
		case "notfound":
			model.addAttribute("description", "Spouštěný proces nenalezen. Zkuste to prosím znova.");
			break;
		default:
			model.addAttribute("description", "Objevila se neznámá chyba. Zkuste prosím opakovat vaší akci.");
			break;
		}

		return "process/error";
	}

}
