package cz.muni.fi.cep.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cz.muni.fi.cep.activiti.notification.service.NotifyHistoryService;
import cz.muni.fi.cep.activiti.radio.service.BroadcastMessageHistoryService;
import cz.muni.fi.cep.api.DTO.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.services.CepHistoryService;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

@Controller
@RequestMapping(value = { "/historie" })
public class HistoryController {
	@Autowired
	private BroadcastMessageHistoryService broadcastMessageHistoryService;

	@Autowired
	private NotifyHistoryService notifyHistoryService;

	@Autowired
	private DefaultHistoryService defaultHistoryService;

	@ModelAttribute("processes")
	public List<String> getProcessList() {
		List<String> processes = new ArrayList<>();
		processes.add("Rozhlas");
		processes.add("Oznámení");
		return processes;
	}

	@RequestMapping(value = { "", "/" })
	public String index() {
		return "history/history";
	}

	@RequestMapping(value = { "/proces" })
	public String proces(
			@RequestParam(value = "proces", defaultValue = "null") String process,
			Model model) {
		CepHistoryService historyService = null;

		if (process.equals("Rozhlas"))
			historyService = broadcastMessageHistoryService;
		if (process.equals("Oznámení"))
			historyService = notifyHistoryService;
		else
			historyService = defaultHistoryService;

		model.addAttribute("processInstances", historyService.getAllInstances());
		if (!process.equals("null"))
			model.addAttribute("process", process);

		return "history/process";
	}

	@RequestMapping(value = { "/instance" })
	public String instance(
			@RequestParam(value = "pid", required = true) String pid,
			@RequestParam(value = "proces", defaultValue = "null") String process,
			Model model) {
		CepHistoryService historyService = null;

		if (process.equals("Rozhlas"))
			historyService = broadcastMessageHistoryService;
		if (process.equals("Oznámení"))
			historyService = notifyHistoryService;
		else
			historyService = defaultHistoryService;

		final CepHistoryProcessInstance hpi = historyService.getDetail(pid);
		model.addAttribute("process", hpi);
		if (!process.equals("null"))
			model.addAttribute("processName", process);

		return "history/instance";
	}

}
