/**
 * 
 */
package cz.muni.fi.cep.api.DTO.history;

import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.history.HistoricActivityInstance;

/**
 * DTO representation of {@link HistoricActivityInstance}
 * 
 * @author Jan Bruzl
 */
public class CepHistoricActivitiInstance implements Serializable, HistoricActivityInstance {
	private static final long serialVersionUID = -531120981999324905L;
	
	private String id;
	private String activitiId;
	private String activitiName;
	private String activitiType;
	private String processDefinitionId;
	private Date time;
	private Date startTime;
	private Date endTime;
	private String processInstanceId;
	private String executionId;
	private String taskId;
	private String tenantId;
	private String calledProcessInstanceId;
	private String assignee;
	private Long durationInMillis;

	@Override
	public Date getTime() {
		return time;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getActivityId() {
		return activitiId;
	}

	@Override
	public String getActivityName() {
		return activitiName;
	}

	@Override
	public String getActivityType() {
		return activitiType;
	}

	@Override
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	@Override
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	@Override
	public String getExecutionId() {
		return executionId;
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public String getCalledProcessInstanceId() {
		return calledProcessInstanceId;
	}

	@Override
	public String getAssignee() {
		return assignee;
	}

	@Override
	public Date getStartTime() {
		return startTime;
	}

	@Override
	public Date getEndTime() {
		return endTime;
	}

	@Override
	public Long getDurationInMillis() {
		return durationInMillis;
	}

	@Override
	public String getTenantId() {
		return tenantId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setActivitiId(String activitiId) {
		this.activitiId = activitiId;
	}

	public void setActivitiName(String activitiName) {
		this.activitiName = activitiName;
	}

	public void setActivitiType(String activitiType) {
		this.activitiType = activitiType;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setCalledProcessInstanceId(String calledProcessInstanceId) {
		this.calledProcessInstanceId = calledProcessInstanceId;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public void setDurationInMillis(Long durationInMillis) {
		this.durationInMillis = durationInMillis;
	}

	@Override
	public String toString() {
		return "CepHistoricActivitiInstance [id=" + id + ", activitiId="
				+ activitiId + ", activitiName=" + activitiName
				+ ", activitiType=" + activitiType + ", processDefinitionId="
				+ processDefinitionId + ", time=" + time + ", startTime="
				+ startTime + ", endTime=" + endTime + ", processInstanceId="
				+ processInstanceId + ", executionId=" + executionId
				+ ", taskId=" + taskId + ", tenantId=" + tenantId
				+ ", calledProcessInstanceId=" + calledProcessInstanceId
				+ ", assignee=" + assignee + ", durationInMillis="
				+ durationInMillis + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activitiId == null) ? 0 : activitiId.hashCode());
		result = prime * result
				+ ((activitiName == null) ? 0 : activitiName.hashCode());
		result = prime * result
				+ ((activitiType == null) ? 0 : activitiType.hashCode());
		result = prime * result
				+ ((assignee == null) ? 0 : assignee.hashCode());
		result = prime
				* result
				+ ((calledProcessInstanceId == null) ? 0
						: calledProcessInstanceId.hashCode());
		result = prime
				* result
				+ ((durationInMillis == null) ? 0 : durationInMillis.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result
				+ ((executionId == null) ? 0 : executionId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((processDefinitionId == null) ? 0 : processDefinitionId
						.hashCode());
		result = prime
				* result
				+ ((processInstanceId == null) ? 0 : processInstanceId
						.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result
				+ ((tenantId == null) ? 0 : tenantId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CepHistoricActivitiInstance other = (CepHistoricActivitiInstance) obj;
		if (activitiId == null) {
			if (other.activitiId != null)
				return false;
		} else if (!activitiId.equals(other.activitiId))
			return false;
		if (activitiName == null) {
			if (other.activitiName != null)
				return false;
		} else if (!activitiName.equals(other.activitiName))
			return false;
		if (activitiType == null) {
			if (other.activitiType != null)
				return false;
		} else if (!activitiType.equals(other.activitiType))
			return false;
		if (assignee == null) {
			if (other.assignee != null)
				return false;
		} else if (!assignee.equals(other.assignee))
			return false;
		if (calledProcessInstanceId == null) {
			if (other.calledProcessInstanceId != null)
				return false;
		} else if (!calledProcessInstanceId
				.equals(other.calledProcessInstanceId))
			return false;
		if (durationInMillis == null) {
			if (other.durationInMillis != null)
				return false;
		} else if (!durationInMillis.equals(other.durationInMillis))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (executionId == null) {
			if (other.executionId != null)
				return false;
		} else if (!executionId.equals(other.executionId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (processDefinitionId == null) {
			if (other.processDefinitionId != null)
				return false;
		} else if (!processDefinitionId.equals(other.processDefinitionId))
			return false;
		if (processInstanceId == null) {
			if (other.processInstanceId != null)
				return false;
		} else if (!processInstanceId.equals(other.processInstanceId))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (tenantId == null) {
			if (other.tenantId != null)
				return false;
		} else if (!tenantId.equals(other.tenantId))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	
}
