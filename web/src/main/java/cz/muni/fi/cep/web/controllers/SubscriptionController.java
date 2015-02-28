package cz.muni.fi.cep.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;
import cz.muni.fi.cep.core.subscriptions.entities.Subscriber;


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
	public String index(Model model) {
		List<Subscriber> subs = new ArrayList<>();
		/*for(String code : subscriptionService.getAllPublishers()) {
			//subs.addAll(subscriptionService.getUserSubscribers(code, null));
		}*/
		model.addAttribute("userSubscriptions", subs);
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
	
	@RequestMapping(value= {"/prihlasit-uzivatele-submit"}, method=RequestMethod.POST)
	public String subscribeUser(
			@RequestParam(required=true, value="user") String userId,
			@RequestParam(required=true, value="publisher") String publisherCode,
			@RequestParam(required=true, value="contactType") ContactType contactType
			) {
		CepUser cepUser = identityService.getCepUserById(Long.parseLong(userId));
		if(cepUser==null) {
			//TODO error message
			return "redirect:/odbery";
		}
		
		subscriptionService.subscribeUser(cepUser, publisherCode, contactType);
		
		return "redirect:/odbery";
	}
	
	@RequestMapping(value= {"/prihlasit-kontakt-submit"}, method=RequestMethod.POST)
	public String subscribeContact(
			@RequestParam(required=true, value="contact") String contact,
			@RequestParam(required=true, value="publisher") String publisherCode,
			@RequestParam(required=true, value="contactType") ContactType contactType) {
		
		subscriptionService.subscribe(contact, publisherCode, contactType);
		
		return "redirect:/odbery";
	}
	
	@RequestMapping("/prehled")
	public String selectPublisher(Model model, @RequestParam(value="publisher", required=false) String publisher) {
		model.addAttribute("publishers", subscriptionService.getAllPublishers());
		if(publisher!= null) {
			Map<String, List<CepUser>> userSubscribers = new HashMap<>();
			userSubscribers.put(ContactType.EMAIL.toString(), subscriptionService.getUserSubscribers(publisher, ContactType.EMAIL));
			userSubscribers.put(ContactType.SMS.toString(), subscriptionService.getUserSubscribers(publisher, ContactType.SMS));
			model.addAttribute("userSubscribers", userSubscribers);
			
			Map<ContactType, List<String>> contactSubscribers = new HashMap<>();
			contactSubscribers.put(ContactType.EMAIL, subscriptionService.getSubscribers(publisher, ContactType.EMAIL));
			contactSubscribers.put(ContactType.SMS, subscriptionService.getSubscribers(publisher, ContactType.SMS));
			model.addAttribute("contactSubscribers", contactSubscribers);
		}
		return "subscriptions/subscribers";
	}
}
