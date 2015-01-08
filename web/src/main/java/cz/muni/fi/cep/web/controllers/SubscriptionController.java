package cz.muni.fi.cep.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.core.subscriptions.api.SubscriptionService;
import cz.muni.fi.cep.core.users.api.IdentityService;


@Controller
@RequestMapping(value= {"/odbery"})
public class SubscriptionController {
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private IdentityService identityService;
	
	@ModelAttribute("contactTypes")
	public List<ContactType> contactTypes(){
		List<ContactType> contactTypes = new ArrayList<>();
		contactTypes.add(ContactType.EMAIL);
		contactTypes.add(ContactType.SMS);
		return contactTypes;
	}
	
	@RequestMapping(value= {""})
	public String index() {
		
		return "subscriptions/publishers";
	}
	
	@RequestMapping(value= {"/udalosti"})
	public String publishers(Model model) {
		model.addAttribute("publishers", subscriptionService.getAllPublishers());
		return "subscriptions/publishers";
	}
	
	@RequestMapping(value= {"/prihlaseni"})
	public String subscribe(Model model) {
		model.addAttribute("publishers", subscriptionService.getAllPublishers());
		model.addAttribute("users", identityService.getAllCepUsers());
		return "subscriptions/subscribe";
	}
	
	@RequestMapping(value= {"/odbery/prihlasit-uzivatele-submit"}, method=RequestMethod.POST)
	public String subscribeUser() {
		//TODO
		return "redirect:/odbery";
	}
	
	@RequestMapping(value= {"/odbery/prihlasit-kontakt-submit"}, method=RequestMethod.POST)
	public String subscribeContact() {
		//TODO
		return "redirect:/odbery";
	}
}
