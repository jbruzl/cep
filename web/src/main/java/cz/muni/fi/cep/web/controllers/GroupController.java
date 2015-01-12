package cz.muni.fi.cep.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cz.muni.fi.cep.core.users.api.IdentityService;
import cz.muni.fi.cep.core.users.entities.CepGroupEntity;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

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
	
	@RequestMapping(value= {"/clenove"})
	public String memebers(Model model, @RequestParam(value="id", required=true)Long id) {
		CepGroupEntity cepGroup = identityService.getGroupById(id);
		if(cepGroup == null) {
			//TODO error message
			return "redirect:/skupiny";
		}
		model.addAttribute("group", cepGroup);
		model.addAttribute("users", identityService.getAllCepUsers());
		return "/groups/members";
	}
	
	@RequestMapping(value= {"/pridat-clena-submit"}, method=RequestMethod.POST)
	public String addMember(Model model, @RequestParam(value="group", required=true) Long groupId, @RequestParam(value="id", required=true) Long userId) {
		CepGroupEntity group = identityService.getGroupById(groupId);
		CepUserEntity user = identityService.getCepUserById(userId);
		if(group== null || user == null) {
			//TODO error message
			return "redirect:/skupiny";
		}
		identityService.createMembership(user, group);
		return "redirect:/skupiny/clenove?id="+groupId;
	}
	
	@RequestMapping(value= {"/odebrat-clena"})
	public String deleteMember( @RequestParam(value="id", required=true) Long groupId, @RequestParam(value="user", required=true) Long userId) {
		CepGroupEntity group = identityService.getGroupById(groupId);
		CepUserEntity user = identityService.getCepUserById(userId);
		if(group== null || user == null) {
			//TODO error message
			return "redirect:/skupiny";
		}
		identityService.deleteMembership(user, group);
		return "redirect:/skupiny/clenove?id="+groupId;
	}
	
	@RequestMapping(value= {"/odebrat"})
	public String deleteGroup( @RequestParam(value="id", required=true) Long groupId) {
		CepGroupEntity group = identityService.getGroupById(groupId);
		if(group== null) {
			//TODO error message
			return "redirect:/skupiny";
		}
		identityService.deleteGroup(group);
		return "redirect:/skupiny";
	}
}
