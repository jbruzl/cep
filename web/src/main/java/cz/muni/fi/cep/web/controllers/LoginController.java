package cz.muni.fi.cep.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.services.users.IdentityService;

@Controller
public class LoginController {
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@RequestMapping("/signup")
	public String signup() {
		
		return "users/signup";
	}
	@RequestMapping(value="/signup-submit", method = RequestMethod.POST)
	public String addSubmit(
			@RequestParam(value = "name", required=true) String name,
			@RequestParam(value = "surname", required=true) String surname,
			@RequestParam(value = "password", required=true) String password,
			@RequestParam(value = "password2", required=true) String password2,
			@RequestParam(value = "email", required=true) String mail,
			@RequestParam(value = "phone") String phone) {

		CepUser user = new CepUser();
		user.setFirstName(name);
		user.setLastName(surname);
		user.setMail(mail);
		user.setPassword(passwordEncoder.encode(password));
		user.setPhoneNumber(phone);
		
		identityService.createUser(user);
		
		return "redirect:/#login";
	}
}
