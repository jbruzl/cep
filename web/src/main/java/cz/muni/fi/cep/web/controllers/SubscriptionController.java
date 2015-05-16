package cz.muni.fi.cep.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
	@RequestMapping(value= {"/moje-odbery"})
	public String mySubscriptions(Model model) {
		String email = ((User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();
		
		CepUser cepUser = identityService.getCepUserByEmail(email);
		List<String> eventsSMS = subscriptionService.getUserSubscriptions(cepUser, ContactType.SMS);
		model.addAttribute("eventsSMS", eventsSMS);
		List<String> eventsMail = subscriptionService.getUserSubscriptions(cepUser, ContactType.EMAIL);
		model.addAttribute("eventsEmail", eventsMail);
		
		model.addAttribute("publishers", subscriptionService.getAllPublishers());
		return "subscriptions/mysubscriptions";
	}
	
	@RequestMapping(value= {"/prihlasit-se-submit"}, method=RequestMethod.POST)
	public String subscribeLoggedUser(
			@RequestParam(required=true, value="publisher") String publisherCode,
			@RequestParam(required=true, value="contactType") ContactType contactType
			) {
		String email = ((User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();
		
		CepUser cepUser = identityService.getCepUserByEmail(email);
		if(cepUser==null) {
			return "redirect:/odbery/moje-odbery";
		}
		
		subscriptionService.subscribeUser(cepUser, publisherCode, contactType);
		
		return "redirect:/odbery/moje-odbery";
	}
	
	@RequestMapping(value= {"/odhlasit-se"}, method=RequestMethod.GET)
	public String unsubscribeMe(@RequestParam(required=true, value="subscription") String id,
			@RequestParam(value="type") String type){
		
		String email = ((User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();
		
		CepUser cepUser = identityService.getCepUserByEmail(email);
		if(cepUser==null) {
			return "redirect:/odbery/moje-odbery";
		}
		ContactType ct = ContactType.fromValue(type);
		subscriptionService.unsubscribeUser(cepUser, id, ct);
		
		return "redirect:/odbery/moje-odbery";
	}
	
	
	@RequestMapping(value= {"/odhlasit-uzivatele"}, method=RequestMethod.GET)
	public String unsubscribe(@RequestParam(required=true, value="subscription") String id,
			@RequestParam(required=true, value="user") String userId,
			final HttpServletRequest request,
			@RequestParam(value="type") String type){
		CepUser cepUser = identityService.getCepUserById(Long.parseLong(userId));
		String referer = request.getHeader("referer");
		if(cepUser==null) {
			return "redirect:" + referer;
		}
		ContactType ct = ContactType.fromValue(type);
		subscriptionService.unsubscribeUser(cepUser, id, ct);

		return "redirect:" + referer;
	}
	
	@RequestMapping(value= {"/odhlasit-kontakt"}, method=RequestMethod.GET)
	public String unsubscribeContact(@RequestParam(required=true, value="subscription") String id,
			@RequestParam(required=true, value="kontakt") String contact,
			final HttpServletRequest request,
			@RequestParam(value="type") String type){
		ContactType ct = ContactType.fromValue(type);
		subscriptionService.unsubscribe(contact, id, ct);
		String referer = request.getHeader("referer");
		return "redirect:" + referer;
	}
	
	@RequestMapping(value= {"/prihlasit-uzivatele-submit"}, method=RequestMethod.POST)
	public String subscribeUser(
			@RequestParam(required=true, value="user") String userId,
			@RequestParam(required=true, value="publisher") String publisherCode,
			@RequestParam(required=true, value="contactType") ContactType contactType,
			final HttpServletRequest request
			) {
		CepUser cepUser = identityService.getCepUserById(Long.parseLong(userId));
		String referer = request.getHeader("referer");
		if(cepUser==null) {
			return "redirect:" + referer;
		}
		
		subscriptionService.subscribeUser(cepUser, publisherCode, contactType);
		
		return "redirect:" + referer;
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
			
			model.addAttribute("selectedPublisher", publisher);
		}
		return "subscriptions/subscribers";
	}
}
