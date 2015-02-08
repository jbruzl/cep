package cz.muni.fi.cep.core.servicemanager.history;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.services.CepHistoryService;

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
		return mapper.map(hpi, CepHistoryProcessInstance.class);
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

}
