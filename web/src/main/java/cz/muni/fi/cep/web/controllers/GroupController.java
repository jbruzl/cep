package cz.muni.fi.cep.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cz.muni.fi.cep.core.users.api.IdentityService;
import cz.muni.fi.cep.core.users.entities.CepGroupEntity;

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
	
	@RequestMapping(value= {"/pridat"})
	public String addGroup(Model model) {
		model.addAttribute("group", new CepGroupEntity());
		return "/groups/add";
	}
	
	@RequestMapping(value= {"/pridat-submit"}, method=RequestMethod.POST)
	public String createGroup(Model model, final CepGroupEntity group) {
		identityService.createGroup(group);
		return "redirect:/skupiny";
	}
}
