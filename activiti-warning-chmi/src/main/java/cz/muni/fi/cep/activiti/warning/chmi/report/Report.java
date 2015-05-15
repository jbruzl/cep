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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation of CHMU weather reports Report element
 * @author Jan Bruzl
 *
 */
@XmlRootElement(name="report")
public class Report implements Serializable {
	private static final long serialVersionUID = 3856076962719535703L;

	private GregorianCalendar creationTime;

	
	private String creationDay;

	
	private String creatingAuthority;
	
	
	private String creatingUser;
	
	
	private String creationPurpose;
	
	
	private String creationPurposeCode;
	
	List<Country> countries = new ArrayList<>();

	public GregorianCalendar getCreationTime() {
		return creationTime;
	}

	@XmlAttribute(name="creation-time", required=true)
	public void setCreationTime(GregorianCalendar creationTime) {
		this.creationTime = creationTime;
	}

	public String getCreationDay() {
		return creationDay;
	}

	@XmlAttribute(name="creation-day", required=false)
	public void setCreationDay(String creationDay) {
		this.creationDay = creationDay;
	}


	public String getCreatingAuthority() {
		return creatingAuthority;
	}
	
	@XmlAttribute(name="creating-authority", required=true)
	public void setCreatingAuthority(String creatingAuthority) {
		this.creatingAuthority = creatingAuthority;
	}

	
	public String getCreatingUser() {
		return creatingUser;
	}
	
	@XmlAttribute(name="creating-user", required=false)
	public void setCreatingUser(String creatingUser) {
		this.creatingUser = creatingUser;
	}

	public String getCreationPurpose() {
		return creationPurpose;
	}

	@XmlAttribute(name="creation-purpose", required=true)
	public void setCreationPurpose(String creationPurpose) {
		this.creationPurpose = creationPurpose;
	}

	public String getCreationPurposeCode() {
		return creationPurposeCode;
	}

	@XmlAttribute(name="creation-purpose-code", required=false)
	public void setCreationPurposeCode(String creationPurposeCode) {
		this.creationPurposeCode = creationPurposeCode;
	}

	public List<Country> getCountries() {
		return countries;
	}
	
	@XmlElement(name="country", type=Country.class,required=true)
	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((countries == null) ? 0 : countries.hashCode());
		result = prime
				* result
				+ ((creatingAuthority == null) ? 0 : creatingAuthority
						.hashCode());
		result = prime * result
				+ ((creatingUser == null) ? 0 : creatingUser.hashCode());
		result = prime * result
				+ ((creationDay == null) ? 0 : creationDay.hashCode());
		result = prime * result
				+ ((creationPurpose == null) ? 0 : creationPurpose.hashCode());
		result = prime
				* result
				+ ((creationPurposeCode == null) ? 0 : creationPurposeCode
						.hashCode());
		result = prime * result
				+ ((creationTime == null) ? 0 : creationTime.hashCode());
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
		Report other = (Report) obj;
		if (countries == null) {
			if (other.countries != null)
				return false;
		} else if (!countries.equals(other.countries))
			return false;
		if (creatingAuthority == null) {
			if (other.creatingAuthority != null)
				return false;
		} else if (!creatingAuthority.equals(other.creatingAuthority))
			return false;
		if (creatingUser == null) {
			if (other.creatingUser != null)
				return false;
		} else if (!creatingUser.equals(other.creatingUser))
			return false;
		if (creationDay == null) {
			if (other.creationDay != null)
				return false;
		} else if (!creationDay.equals(other.creationDay))
			return false;
		if (creationPurpose == null) {
			if (other.creationPurpose != null)
				return false;
		} else if (!creationPurpose.equals(other.creationPurpose))
			return false;
		if (creationPurposeCode == null) {
			if (other.creationPurposeCode != null)
				return false;
		} else if (!creationPurposeCode.equals(other.creationPurposeCode))
			return false;
		if (creationTime == null) {
			if (other.creationTime != null)
				return false;
		} else if (!creationTime.equals(other.creationTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Report [creationTime=" + creationTime + ", creationDay="
				+ creationDay + ", creatingAuthority=" + creatingAuthority
				+ ", creatingUser=" + creatingUser + ", creationPurpose="
				+ creationPurpose + ", creationPurposeCode="
				+ creationPurposeCode + ", countries=" + countries + "]";
	}


}
