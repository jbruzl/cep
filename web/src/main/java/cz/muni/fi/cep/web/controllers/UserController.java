package cz.muni.fi.cep.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cz.muni.fi.cep.core.users.api.IdentityService;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

@Controller
@RequestMapping(value = { "/uzivatele" })
public class UserController {
	@Autowired
	private IdentityService identityService;

	@RequestMapping(value = { "" })
	public String index(Model model) {
		model.addAttribute("users", identityService.getAllCepUsers());
		return "users";
	}

	@RequestMapping(value = { "/pridat" })
	public String add() {
		return "users/add";
	}

	@RequestMapping(value = { "/pridat-submit" }, method = RequestMethod.POST)
	public String addSubmit(
			@RequestParam(value = "name") String name,
			@RequestParam(value = "surname") String surname,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "password2") String password2,
			@RequestParam(value = "email") String mail,
			@RequestParam(value = "phone") String phone) {

		CepUserEntity user = new CepUserEntity();
		user.setFirstName(name);
		user.setLastName(surname);
		user.setEmail(mail);
		user.setPassword(password);
		user.setPhoneNumber(phone);
		
		identityService.createUser(user);
		return "redirect:/uzivatele";
	}
	
	@RequestMapping(value= {"/detail"})
	public String detail(Model model, @RequestParam(value="id", required=true) Long id) {
		CepUserEntity cepUserEntity = identityService.getCepUserById(id);
		if(cepUserEntity==null) {
			//TODO error message
			return "users";
		}
		model.addAttribute("user", cepUserEntity);
		return "users/detail";
	}
	
	@RequestMapping(value= {"/delete"})
	public String delete(Model model, @RequestParam(value="id", required=true) Long id) {
		CepUserEntity cepUserEntity = identityService.getCepUserById(id);
		if(cepUserEntity==null) {
			//TODO error message
			return "users";
		}
		identityService.deleteUser(cepUserEntity);
		return "users";
	}
	
	@RequestMapping(value= {"/edit"})
	public String edit(Model model, @RequestParam(value="id", required=true) Long id) {
		CepUserEntity cepUserEntity = identityService.getCepUserById(id);
		if(cepUserEntity==null) {
			//TODO error message
			return "users";
		}
		model.addAttribute("user", cepUserEntity);
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

		CepUserEntity user = identityService.getCepUserById(Long.parseLong(id));
		if(user==null) {
			//TODO error message
			return "users"; 
		}
		user.setFirstName(name);
		user.setLastName(surname);
		user.setEmail(mail);
		user.setPassword(password);
		user.setPhoneNumber(phone);
		
		identityService.updateUser(user);
		return "redirect:/uzivatele";
	}
}
