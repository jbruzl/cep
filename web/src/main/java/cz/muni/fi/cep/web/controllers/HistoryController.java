package cz.muni.fi.cep.web.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cz.muni.fi.cep.api.DTO.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.services.CepHistoryService;
import cz.muni.fi.cep.api.services.CepProcessService;
import cz.muni.fi.cep.api.services.CepProcessServiceManager;
import cz.muni.fi.cep.core.servicemanager.history.DefaultHistoryService;

@Controller
@RequestMapping(value = { "/historie" })
public class HistoryController {
	@Autowired
	private CepProcessServiceManager processServiceManager;

	@Autowired
	private DefaultHistoryService defaultHistoryService;

	@ModelAttribute("processes")
	public List<CepProcessService> getProcessList() {
		return processServiceManager.getServices();
	}

	@RequestMapping(value = { "", "/" })
	public String index() {
		return "history/history";
	}

	@RequestMapping(value = { "/proces/{process}" })
	public String proces(
			@PathVariable(value="process") String process,
			Model model) {
		CepHistoryService historyService = null;

		CepProcessService service = processServiceManager
				.getServiceByKey(process);
		if (service != null)
			historyService = service.getHistoryService();
		else
			historyService = defaultHistoryService;
		
		Map<String, Integer> endStateStatistic = historyService.getEndStateStatistic();
		Integer endStatisticTotal = 0;
		for(Integer endStateCount : endStateStatistic.values()) {
			endStatisticTotal += endStateCount;
		}
		model.addAttribute("endStatistic", endStateStatistic);
		model.addAttribute("endStatisticTotal", endStatisticTotal);

		model.addAttribute("processInstances", historyService.getAllInstances());
		if (!process.equals("null")) {
			model.addAttribute("processName", service.getName());
			model.addAttribute("processKey", service.getKey());
		}else {
			model.addAttribute("processName", "Všechny procesy");
			model.addAttribute("processKey", service.getKey());
		}

		return "history/process";
	}

	@RequestMapping(value = { "/instance/{process}/{pid}" })
	public String instance(
			@PathVariable("pid")String pid,
			@PathVariable("process") String process,
			Model model) {
		CepHistoryService historyService = null;

		CepProcessService service = processServiceManager
				.getServiceByKey(process);
		if (service != null)
			historyService = service.getHistoryService();
		else
			historyService = defaultHistoryService;

		final CepHistoryProcessInstance hpi = historyService.getDetail(pid);
		model.addAttribute("process", hpi);
		if (!process.equals("null"))
			model.addAttribute("processName", service.getName());

		return "history/instance";
	}

}

