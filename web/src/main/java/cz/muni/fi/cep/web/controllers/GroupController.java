package cz.muni.fi.cep.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cz.muni.fi.cep.core.users.api.IdentityService;

@Controller
@RequestMapping(value = { "/skupiny" })
public class GroupController {
	@Autowired
	private IdentityService identityService;
	
	@RequestMapping(value= {""})
	public String index(Model model) {
		model.addAttribute("groups", identityService.getAllGroups());
		return "/groups/groups";
	}
}
