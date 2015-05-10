package cz.muni.fi.cep.web.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.muni.fi.cep.api.DTO.history.CepHistoricActivitiInstance;
import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.services.servicemanager.CepHistoryService;
import cz.muni.fi.cep.api.services.servicemanager.CepProcessService;
import cz.muni.fi.cep.api.services.servicemanager.CepProcessServiceManager;
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
	public String proces(@PathVariable(value = "process") String process,
			Model model) {
		CepHistoryService historyService = null;

		CepProcessService service = processServiceManager
				.getServiceByKey(process);
		if (service != null)
			historyService = service.getHistoryService();
		else
			historyService = defaultHistoryService;

		Map<String, Integer> endStateStatistic = historyService
				.getEndStateStatistic();
		Integer endStatisticTotal = 0;
		for (Integer endStateCount : endStateStatistic.values()) {
			endStatisticTotal += endStateCount;
		}
		model.addAttribute("endStatistic", endStateStatistic);
		model.addAttribute("endStatisticTotal", endStatisticTotal);

		model.addAttribute("processInstances", historyService.getAllInstances());
		if (!process.equals("null")) {
			model.addAttribute("processName", service.getName());
			model.addAttribute("processKey", service.getKey());
		} else {
			model.addAttribute("processName", "Všechny procesy");
			model.addAttribute("processKey", service.getKey());
		}

		return "history/process";
	}

	@RequestMapping(value = { "/instance/{process}/{pid}" })
	public String instance(@PathVariable("pid") String pid,
			@PathVariable("process") String process, Model model) {
		CepHistoryService historyService = null;

		CepProcessService service = processServiceManager
				.getServiceByKey(process);
		if (service != null)
			historyService = service.getHistoryService();
		else
			historyService = defaultHistoryService;

		final CepHistoryProcessInstance hpi = historyService.getDetail(pid);
		CepHistoricActivitiInstance startActivity = null;
		CepHistoricActivitiInstance endActivity = null;
		for(CepHistoricActivitiInstance chai : hpi.getActivitiInstances()){
			if(chai.getActivityId().equals(hpi.getStartActivityId())){
				startActivity = chai;
			}
			if(chai.getActivityId().equals(hpi.getEndActivityId())){
				endActivity = chai;
			}
		}
		model.addAttribute("startActivity", startActivity);
		model.addAttribute("endActivity", endActivity);
		model.addAttribute("process", hpi);
		if (!process.equals("null"))
			model.addAttribute("processName", service.getName());
		model.addAttribute("processKey", process);

		return "history/instance";
	}

	@RequestMapping(value = { "/diagram/{process}"})
	@ResponseBody
	public ResponseEntity<byte[]> displayProcessDiagram(
			@PathVariable("process") String process){
		return displayProcessInstanceDiagram(null, process);
	}
	
	@RequestMapping(value = { "/diagram/{process}/{pid}"})
	@ResponseBody
	public ResponseEntity<byte[]> displayProcessInstanceDiagram(
			@PathVariable("pid") String pid,
			@PathVariable("process") String process) {
		CepProcessService service = processServiceManager
				.getServiceByKey(process);
		if (service == null)
			return null;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.IMAGE_PNG);
		responseHeaders.set("Content-Disposition",
				"attachment; filename=diagram.png");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] imageInByte = null;
		try {
			BufferedImage diagram;
			if (pid != null)
				diagram = service.getDiagram(pid);
			else
				diagram = service.getDiagram();
			ImageIO.write(diagram, "png", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			return null;
		}
		return new ResponseEntity<byte[]>(imageInByte, responseHeaders,
				HttpStatus.OK);

	}
}
