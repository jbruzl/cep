/**
 * 
 */
package cz.muni.fi.cep.web.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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

import cz.muni.fi.cep.api.form.CepCheckboxFormType;
import cz.muni.fi.cep.api.form.CepFileFormType;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.form.CepTextFormType;
import cz.muni.fi.cep.api.services.servicemanager.CepProcessService;
import cz.muni.fi.cep.api.services.servicemanager.CepProcessServiceManager;

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
			}
			if (cfp.getType() instanceof CepTextFormType) {
				String parameter = httpServletRequest.getParameter(cfp.getId());
				cfp.setInput(parameter);
			}
			if (cfp.getType() instanceof CepCheckboxFormType) {
				if (httpServletRequest.getParameter(cfp.getId()) == null)
					cfp.setInput(false);
				else
					cfp.setInput(true);
			}
		}
		ProcessInstance pi = service.runProcess(startForm);
		if (pi == null) {
			return "redirect:/proces/error?proces=" + key + "&state=startfail";
		}
		List<Task> availableTasks = service.getAvailableTasks(pi.getId());
		if(availableTasks.size()>0)
			return "redirect:/proces/task/"+service.getProcessKey()+"/"+availableTasks.get(0).getId();
		else
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
			model.addAttribute("description",
					"Vyskytla se chyba během nahrání souboru. Zkuste to prosím znova.");
			break;
		case "startfail":
			model.addAttribute("description",
					"Nepodařilo se spustit proces. Zkuste to prosím znova.");
			break;
		case "notfound":
			model.addAttribute("description",
					"Spouštěný proces nenalezen. Zkuste to prosím znova.");
			break;
		default:
			model.addAttribute("description",
					"Objevila se neznámá chyba. Zkuste prosím opakovat vaší akci.");
			break;
		}

		return "process/error";
	}

	@RequestMapping("/task/{processkey}/{id}")
	public String task(@PathVariable("processkey") String processkey,
			@PathVariable("id") String id, Model model) {
		CepProcessService service = processServiceManager
				.getServiceByKey(processkey);
		if (service == null) {
			return "process/error?proces=" + processkey + "&state=notfound";
		}

		CepFormData taskForm = service.getTaskForm(id);
		model.addAttribute("startForm", taskForm);

		model.addAttribute("process", service.getName());
		model.addAttribute("key", processkey);
		model.addAttribute("taskId", id);
		model.addAttribute("description", service.getDescription());

		return "process/continue";
	}

	@RequestMapping("/task/submit/{processkey}/{id}")
	public String taskSubmit(@PathVariable("processkey") String key,
			@PathVariable("id") String id, Model model,
			HttpServletRequest httpServletRequest) {

		CepProcessService service = processServiceManager.getServiceByKey(key);
		if (service == null) {
			return "process/error?proces=" + key + "&state=notfound";
		}

		CepFormData form = service.getTaskForm(id);

		for (FormProperty fp : form.getFormProperties()) {
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

		String processInstanceId = service.complete(id, form);
		
		List<Task> availableTasks = service.getAvailableTasks(processInstanceId);
		if(availableTasks.size()>0)
			return "redirect:/proces/task/"+service.getProcessKey()+"/"+availableTasks.get(0).getId();
		else
			return "redirect:/historie/instance/" + key + "/" + processInstanceId;
	}
	
	@RequestMapping("/tasks")
	public String availableTasks(Model model){
		List<Task> availableTasks = processServiceManager.getAvailableTasks();
		
		model.addAttribute("tasks", availableTasks);
		
		return "process/tasks";
	}

}
