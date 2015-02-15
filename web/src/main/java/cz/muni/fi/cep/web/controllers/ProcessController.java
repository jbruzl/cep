/**
 * 
 */
package cz.muni.fi.cep.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cz.muni.fi.cep.api.DTO.forms.CepFormData;
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
	
	
	@RequestMapping("/start/{key}")
	public String startForm(@PathVariable("key")String key, Model model) {
		CepProcessService service = processServiceManager.getServiceByKey(key);
		if(service == null) {
			return "process/error?proces="+key+"&state=notfound";
		}
		
		CepFormData startForm = service.getStartForm();
		model.addAttribute("startForm", startForm);
		
		model.addAttribute("process", service.getName());
		model.addAttribute("description", service.getDescription());
		
		return "process/start";
	}
}
