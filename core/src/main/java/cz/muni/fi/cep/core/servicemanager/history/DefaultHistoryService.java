package cz.muni.fi.cep.core.servicemanager.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.history.CepHistoricActivitiInstance;
import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.services.servicemanager.CepHistoryService;

/**
 * Default implementation of {@link CepHistoryService}.
 * 
 * Process specific implementation should extend this class and override
 * {@link #getDetail(String)} method with customized implementation.
 * 
 * @author Jan Bruzl
 */
@Service("defaultHistoryService")
public class DefaultHistoryService implements CepHistoryService {
	@Autowired
	private HistoryService historyService;
	@Autowired
	private RepositoryService repositoryService;;

	@Autowired
	private Mapper mapper;

	protected String processDefinitionKey = null;

	@Override
	public List<CepHistoryProcessInstance> getAllInstances() {
		List<CepHistoryProcessInstance> cepHPIList = new ArrayList<>();
		List<HistoricProcessInstance> hpiList = getProcessInstanceQuery()
				.list();

		for (HistoricProcessInstance hpi : hpiList) {
			CepHistoryProcessInstance cepHPI = mapper.map(hpi,
					CepHistoryProcessInstance.class);

			cepHPIList.add(cepHPI);
		}

		return cepHPIList;
	}

	@Override
	public List<CepHistoryProcessInstance> getFinishedInstances() {
		List<CepHistoryProcessInstance> cepHPIList = new ArrayList<>();
		List<HistoricProcessInstance> hpiList = getProcessInstanceQuery()
				.finished().list();

		for (HistoricProcessInstance hpi : hpiList) {
			CepHistoryProcessInstance cepHPI = mapper.map(hpi,
					CepHistoryProcessInstance.class);

			cepHPIList.add(cepHPI);
		}

		return cepHPIList;
	}

	@Override
	public List<CepHistoryProcessInstance> getRunningInstances() {
		List<CepHistoryProcessInstance> cepHPIList = new ArrayList<>();
		List<HistoricProcessInstance> hpiList = getProcessInstanceQuery()
				.unfinished().list();

		for (HistoricProcessInstance hpi : hpiList) {
			CepHistoryProcessInstance cepHPI = mapper.map(hpi,
					CepHistoryProcessInstance.class);

			cepHPIList.add(cepHPI);
		}

		return cepHPIList;
	}

	@Override
	public CepHistoryProcessInstance getDetail(String pid)
			throws IllegalArgumentException {
		HistoricProcessInstance hpi = getProcessInstanceQuery()
				.processInstanceId(pid).singleResult();
		if (hpi == null)
			return null;
		CepHistoryProcessInstance chpi = mapper.map(hpi, CepHistoryProcessInstance.class);
		
		
		List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery().processInstanceId(pid).orderByHistoricActivityInstanceStartTime().asc().list();
		
		for (HistoricActivityInstance hai : haiList) {
			chpi.addActivitiInstances(mapper.map(hai, CepHistoricActivitiInstance.class));
		}
		
		return chpi;
	}

	/**
	 * Returns properly created process instance query.
	 * 
	 * Checks out if {@link #processDefinitionKey} is set, if yes then returns
	 * query for set {@link #processDefinitionKey}.
	 * 
	 * @return {@link HistoricProcessInstanceQuery}
	 */
	private HistoricProcessInstanceQuery getProcessInstanceQuery() {
		HistoricProcessInstanceQuery query = historyService
				.createHistoricProcessInstanceQuery()
				.orderByProcessInstanceStartTime().asc();

		if (processDefinitionKey != null)
			query.processDefinitionKey(processDefinitionKey);

		return query;
	}

	@Override
	public Map<String, Integer> getEndStateStatistic(){
		Map<String, Integer> endStateStatistic = new HashMap<>();
		String processDefinitionId = null;
		
		if(processDefinitionKey!=null) {
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
			if(processDefinition != null) {
				processDefinitionId = processDefinition.getId();
			}
			 
		}		
		
		HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery().activityType("endEvent");
		if(processDefinitionId!=null)
			historicActivityInstanceQuery = historicActivityInstanceQuery.processDefinitionId(processDefinitionId);
		List<HistoricActivityInstance> haiList = historicActivityInstanceQuery.list();
		
		for(HistoricActivityInstance hai : haiList) {
			if(endStateStatistic.containsKey(hai.getActivityName())){
				endStateStatistic.put(hai.getActivityName(), endStateStatistic.get(hai.getActivityName())+1);
			}else {
				endStateStatistic.put(hai.getActivityName(),1);
			}
		}

		return endStateStatistic;
	}
}
