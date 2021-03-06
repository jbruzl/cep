package cz.muni.fi.cep.web.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.services.users.IdentityService;

@Controller
@RequestMapping(value = { "/uzivatele" })
public class UserController {
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@RequestMapping(value = { "", "/" })
	public String index(Model model) {
		model.addAttribute("users", identityService.getAllCepUsers());
		return "users/users";
	}

	@RequestMapping(value = { "/pridat" })
	public String add(Model model) {
		model.addAttribute("groups", identityService.getAllGroups());
		return "users/add";
	}

	@RequestMapping(value = { "/pridat-submit" }, method = RequestMethod.POST)
	public String addSubmit(
			@RequestParam(value = "name") String name,
			@RequestParam(value = "surname") String surname,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "password2") String password2,
			@RequestParam(value = "email") String mail,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "groups") List<String> groups) {

		CepUser user = new CepUser();
		user.setFirstName(name);
		user.setLastName(surname);
		user.setMail(mail);
		user.setPassword(password);
		user.setPhoneNumber(phone);
		
		identityService.createUser(user);
		for(String group : groups) {
			CepGroup cepGroup = identityService.getGroupById(Long.parseLong(group));
			if(cepGroup == null)
				continue;
			identityService.createMembership(user, cepGroup);
		}
		return "redirect:/uzivatele";
	}
	
	@RequestMapping(value= {"/detail"})
	public String detail(Model model, @RequestParam(value="id", required=true) Long id) {
		CepUser cepUser = identityService.getCepUserById(id);
		if(cepUser==null) {
			return "users";
		}
		model.addAttribute("user", cepUser);
		model.addAttribute("groups", identityService.getMemberships(cepUser));
		return "users/detail";
	}
	
	@RequestMapping(value= {"/delete"})
	public String delete(Model model, @RequestParam(value="id", required=true) Long id) {
		CepUser cepUser = identityService.getCepUserById(id);
		if(cepUser==null) {
			return "redirect:/uzivatele";
		}
		identityService.deleteUser(cepUser);
		return "redirect:/uzivatele";
	}
	
	@RequestMapping(value= {"/edit"})
	public String edit(Model model, @RequestParam(value="id", required=true) Long id) {
		CepUser cepUser = identityService.getCepUserById(id);
		if(cepUser==null) {
			return "users";
		}
		model.addAttribute("user", cepUser);
		model.addAttribute("groups", identityService.getMemberships(cepUser));
		return "users/edit";
	}
	
	@RequestMapping(value = { "/edit-submit" }, method = RequestMethod.POST)
	public String editSubmit(
			@RequestParam(value = "name") String name,
			@RequestParam(value = "surname") String surname,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "password2") String password2,
			@RequestParam(value = "email") String mail,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "id") String id) {

		CepUser user = identityService.getCepUserById(Long.parseLong(id));
		if(user==null) {
			return "redirect:/uzivatele";
		}
		user.setFirstName(name);
		user.setLastName(surname);
		user.setMail(mail);
		if(password.equals(password2) && !password.isEmpty())
			user.setPassword(passwordEncoder.encode(password));
		user.setPhoneNumber(phone);
		
		identityService.updateUser(user);
		return "redirect:/uzivatele";
	}
	
	@RequestMapping(value= {"/mujucet"})
	public String myProfile(Model model) {
		String email = ((User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();
		
		CepUser cepUser = identityService.getCepUserByEmail(email);
		if(cepUser==null) {
			return "users";
		}
		model.addAttribute("user", cepUser);
		model.addAttribute("groups", identityService.getMemberships(cepUser));
		return "users/edit";
	}
	
	@RequestMapping(value= {"/odebrat-skupinu"})
	public String deleteMember( @RequestParam(value="id", required=true) Long groupId, @RequestParam(value="user", required=true) Long userId) {
		CepGroup group = identityService.getGroupById(groupId);
		CepUser user = identityService.getCepUserById(userId);
		if(group== null || user == null) {
			return "redirect:/skupiny";
		}
		identityService.deleteMembership(user, group);
		return "redirect:/uzivatele/edit?id="+userId;
	}
	
}
