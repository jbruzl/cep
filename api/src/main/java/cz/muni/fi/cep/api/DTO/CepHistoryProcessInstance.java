package cz.muni.fi.cep.api.DTO;

import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.history.HistoricProcessInstance;

/**
 * DTO representation of {@link HistoricProcessInstance}.
 * 
 * @author Jan Bruzl
 */
public class CepHistoryProcessInstance implements Serializable {
	private static final long serialVersionUID = 8934514677748723859L;

	private String processDefinitionId;
	private String processInstanceId;
	private Date startTime;
	private Date endTime;
	private Long durationInMillis;
	private String endActivityId;
	private String businessKey;
	private String startUserId;
	private String startActivityId;
	private String superProcessInstanceId;
	private String name;
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public Long getDurationInMillis() {
		return durationInMillis;
	}
	public String getEndActivityId() {
		return endActivityId;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public String getStartUserId() {
		return startUserId;
	}
	public String getStartActivityId() {
		return startActivityId;
	}
	public String getSuperProcessInstanceId() {
		return superProcessInstanceId;
	}
	public String getName() {
		return name;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public void setDurationInMillis(Long durationInMillis) {
		this.durationInMillis = durationInMillis;
	}
	public void setEndActivityId(String endActivityId) {
		this.endActivityId = endActivityId;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public void setStartUserId(String startUserId) {
		this.startUserId = startUserId;
	}
	public void setStartActivityId(String startActivityId) {
		this.startActivityId = startActivityId;
	}
	public void setSuperProcessInstanceId(String superProcessInstanceId) {
		this.superProcessInstanceId = superProcessInstanceId;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "CepHistoryProcessInstance [processDefinitionId="
				+ processDefinitionId + ", processInstanceId="
				+ processInstanceId + ", startTime=" + startTime + ", endTime="
				+ endTime + ", durationInMillis=" + durationInMillis
				+ ", endActivityId=" + endActivityId + ", businessKey="
				+ businessKey + ", startUserId=" + startUserId
				+ ", startActivityId=" + startActivityId
				+ ", superProcessInstanceId=" + superProcessInstanceId
				+ ", name=" + name + "]";
	}

	
}
