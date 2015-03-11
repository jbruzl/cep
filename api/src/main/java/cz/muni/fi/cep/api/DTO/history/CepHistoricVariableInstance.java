/**
 * 
 */
package cz.muni.fi.cep.api.DTO.history;

import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.history.HistoricVariableInstance;

/**
 * DTO representation of {@link HistoricVariableInstance}
 * 
 * @author Jan Bruzl
 */
public class CepHistoricVariableInstance implements Serializable,
		HistoricVariableInstance {
	private static final long serialVersionUID = 7967945594539463487L;
	private Date time;
	private String id;
	private String variableName;
	private String variableTypeName;
	private Object value;
	private String processInstanceId;
	private String taskId;
	private Date createTime; 
	private Date lastUpdatedTime;

	@Override
	public Date getTime() {
		return time;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getVariableName() {
		return variableName;
	}

	@Override
	public String getVariableTypeName() {
		return variableTypeName;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public void setVariableTypeName(String variableTypeName) {
		this.variableTypeName = variableTypeName;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	@Override
	public String toString() {
		return "CepHistoricVariableInstance [time=" + time + ", id=" + id
				+ ", variableName=" + variableName + ", variableTypeName="
				+ variableTypeName + ", value=" + value
				+ ", processInstanceId=" + processInstanceId + ", taskId="
				+ taskId + ", createTime=" + createTime + ", lastUpdatedTime="
				+ lastUpdatedTime + "]";
	}	

}
