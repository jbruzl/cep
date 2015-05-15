/**
 * 
 */
package cz.muni.fi.cep.activiti.warning.chmi.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representation of CHMU weather reports Region element
 * @author Jan Bruzl
 *
 */
public class Region implements Serializable {
	private static final long serialVersionUID = 4251818353735860263L;

	private String name;

	private String code;

	private String awarenessLevel;

	private String awarenessLevelCode;

	private GregorianCalendar startTime;

	private GregorianCalendar endTime;

	private String startDay;

	private String endDay;
	
	private List<Situation> situations = new ArrayList<>();

	public String getName() {
		return name;
	}

	@XmlAttribute(name="name", required=false)
	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	@XmlAttribute(name="code", required=true)
	public void setCode(String code) {
		this.code = code;
	}

	public String getAwarenessLevel() {
		return awarenessLevel;
	}

	@XmlAttribute(name="awareness-level", required=true)
	public void setAwarenessLevel(String awarenessLevel) {
		this.awarenessLevel = awarenessLevel;
	}

	public String getAwarenessLevelCode() {
		return awarenessLevelCode;
	}

	@XmlAttribute(name="awareness-level-code", required=true)
	public void setAwarenessLevelCode(String awarenessLevelCode) {
		this.awarenessLevelCode = awarenessLevelCode;
	}

	public GregorianCalendar getStartTime() {
		return startTime;
	}

	@XmlAttribute(name="start-time", required=false)
	public void setStartTime(GregorianCalendar startTime) {
		this.startTime = startTime;
	}

	public GregorianCalendar getEndTime() {
		return endTime;
	}

	@XmlAttribute(name="end-time", required=false)
	public void setEndTime(GregorianCalendar endTime) {
		this.endTime = endTime;
	}

	public String getStartDay() {
		return startDay;
	}

	@XmlAttribute(name="start-day", required=false)
	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}

	public String getEndDay() {
		return endDay;
	}

	@XmlAttribute(name="end-day", required=false)
	public void setEndDay(String endDay) {
		this.endDay = endDay;
	}

	public List<Situation> getSituations() {
		return situations;
	}

	@XmlElement(name="situation", type=Situation.class, required=false)
	public void setSituations(List<Situation> situations) {
		this.situations = situations;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((awarenessLevel == null) ? 0 : awarenessLevel.hashCode());
		result = prime
				* result
				+ ((awarenessLevelCode == null) ? 0 : awarenessLevelCode
						.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((endDay == null) ? 0 : endDay.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((situations == null) ? 0 : situations.hashCode());
		result = prime * result
				+ ((startDay == null) ? 0 : startDay.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
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
		Region other = (Region) obj;
		if (awarenessLevel == null) {
			if (other.awarenessLevel != null)
				return false;
		} else if (!awarenessLevel.equals(other.awarenessLevel))
			return false;
		if (awarenessLevelCode == null) {
			if (other.awarenessLevelCode != null)
				return false;
		} else if (!awarenessLevelCode.equals(other.awarenessLevelCode))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (endDay == null) {
			if (other.endDay != null)
				return false;
		} else if (!endDay.equals(other.endDay))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (situations == null) {
			if (other.situations != null)
				return false;
		} else if (!situations.equals(other.situations))
			return false;
		if (startDay == null) {
			if (other.startDay != null)
				return false;
		} else if (!startDay.equals(other.startDay))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Region [name=" + name + ", code=" + code + ", awarenessLevel="
				+ awarenessLevel + ", awarenessLevelCode=" + awarenessLevelCode
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", startDay=" + startDay + ", endDay=" + endDay
				+ ", situations=" + situations + "]";
	}

	
	

}
