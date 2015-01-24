/**
 * 
 */
package cz.muni.fi.cep.warning.chmi.report;

import java.io.Serializable;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Representation of CHMI weather reports Situation element
 * 
 * @author Jan Bruzl
 *
 */
public class Situation implements Serializable {
	private static final long serialVersionUID = 2730382898942849486L;

	private String awarenessClass;

	private String awarenessLevel;

	private String awarenessLevelCode;

	private String startDay;

	private String endDay;

	private String awarenessTypeGroup;

	private String awarenessType;

	private String awarenessTypeCode;

	private String startElevation;

	private String endElevation;

	private String subregionTypes;

	private String districts;

	private String catchments;

	private String rivers;

	private String stations;

	private String nmsUrl;

	private String informationUrl;

	private GregorianCalendar startTime;

	private GregorianCalendar endTime;

	private Boolean displayAwarenessType;

	private Boolean backgroundImage;

	private String backgroundImageUrl;

	public String getAwarenessClass() {
		return awarenessClass;
	}

	@XmlAttribute(name="awareness-class", required=false)
	public void setAwarenessClass(String awarenessClass) {
		this.awarenessClass = awarenessClass;
	}

	public String getAwarenessLevel() {
		return awarenessLevel;
	}
	
	@XmlAttribute(name="awareness-level", required=false)
	public void setAwarenessLevel(String awarenessLevel) {
		this.awarenessLevel = awarenessLevel;
	}

	public String getAwarenessLevelCode() {
		return awarenessLevelCode;
	}

	@XmlAttribute(name="awareness-level-code", required=false)
	public void setAwarenessLevelCode(String awarenessLevelCode) {
		this.awarenessLevelCode = awarenessLevelCode;
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

	public String getAwarenessTypeGroup() {
		return awarenessTypeGroup;
	}

	@XmlAttribute(name="awareness-type-group", required=false)
	public void setAwarenessTypeGroup(String awarenessTypeGroup) {
		this.awarenessTypeGroup = awarenessTypeGroup;
	}

	public String getAwarenessType() {
		return awarenessType;
	}

	@XmlAttribute(name="awareness-type", required=false)
	public void setAwarenessType(String awarenessType) {
		this.awarenessType = awarenessType;
	}

	public String getAwarenessTypeCode() {
		return awarenessTypeCode;
	}

	@XmlAttribute(name="awareness-type-code", required=false)
	public void setAwarenessTypeCode(String awarenessTypeCode) {
		this.awarenessTypeCode = awarenessTypeCode;
	}

	public String getStartElevation() {
		return startElevation;
	}

	@XmlAttribute(name="start-elevation", required=false)
	public void setStartElevation(String startElevation) {
		this.startElevation = startElevation;
	}

	public String getEndElevation() {
		return endElevation;
	}

	@XmlAttribute(name="end-elevation", required=false)
	public void setEndElevation(String endElevation) {
		this.endElevation = endElevation;
	}

	public String getSubregionTypes() {
		return subregionTypes;
	}

	@XmlAttribute(name="subregion-types", required=false)
	public void setSubregionTypes(String subregionTypes) {
		this.subregionTypes = subregionTypes;
	}

	public String getDistricts() {
		return districts;
	}
	
	@XmlAttribute(name="districts", required=false)
	public void setDistricts(String districts) {
		this.districts = districts;
	}

	public String getCatchments() {
		return catchments;
	}

	@XmlAttribute(name="catchments", required=false)
	public void setCatchments(String catchments) {
		this.catchments = catchments;
	}

	public String getRivers() {
		return rivers;
	}

	@XmlAttribute(name="rivers", required=false)
	public void setRivers(String rivers) {
		this.rivers = rivers;
	}

	public String getStations() {
		return stations;
	}

	@XmlAttribute(name="stations", required=false)
	public void setStations(String stations) {
		this.stations = stations;
	}

	public String getNmsUrl() {
		return nmsUrl;
	}

	@XmlAttribute(name="nms-url", required=false)
	public void setNmsUrl(String nmsUrl) {
		this.nmsUrl = nmsUrl;
	}

	public String getInformationUrl() {
		return informationUrl;
	}

	@XmlAttribute(name="informations-url", required=false)
	public void setInformationUrl(String informationUrl) {
		this.informationUrl = informationUrl;
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

	public Boolean getDisplayAwarenessType() {
		return displayAwarenessType;
	}

	@XmlAttribute(name="display-awareness-type", required=false)
	public void setDisplayAwarenessType(Boolean displayAwarenessType) {
		this.displayAwarenessType = displayAwarenessType;
	}

	public Boolean getBackgroundImage() {
		return backgroundImage;
	}

	@XmlAttribute(name="background-image", required=false)
	public void setBackgroundImage(Boolean backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public String getBackgroundImageUrl() {
		return backgroundImageUrl;
	}

	@XmlAttribute(name="background-image-url", required=false)
	public void setBackgroundImageUrl(String backgroundImageUrl) {
		this.backgroundImageUrl = backgroundImageUrl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((awarenessClass == null) ? 0 : awarenessClass.hashCode());
		result = prime * result
				+ ((awarenessLevel == null) ? 0 : awarenessLevel.hashCode());
		result = prime
				* result
				+ ((awarenessLevelCode == null) ? 0 : awarenessLevelCode
						.hashCode());
		result = prime * result
				+ ((awarenessType == null) ? 0 : awarenessType.hashCode());
		result = prime
				* result
				+ ((awarenessTypeCode == null) ? 0 : awarenessTypeCode
						.hashCode());
		result = prime
				* result
				+ ((awarenessTypeGroup == null) ? 0 : awarenessTypeGroup
						.hashCode());
		result = prime * result
				+ ((backgroundImage == null) ? 0 : backgroundImage.hashCode());
		result = prime
				* result
				+ ((backgroundImageUrl == null) ? 0 : backgroundImageUrl
						.hashCode());
		result = prime * result
				+ ((catchments == null) ? 0 : catchments.hashCode());
		result = prime
				* result
				+ ((displayAwarenessType == null) ? 0 : displayAwarenessType
						.hashCode());
		result = prime * result
				+ ((districts == null) ? 0 : districts.hashCode());
		result = prime * result + ((endDay == null) ? 0 : endDay.hashCode());
		result = prime * result
				+ ((endElevation == null) ? 0 : endElevation.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result
				+ ((informationUrl == null) ? 0 : informationUrl.hashCode());
		result = prime * result + ((nmsUrl == null) ? 0 : nmsUrl.hashCode());
		result = prime * result + ((rivers == null) ? 0 : rivers.hashCode());
		result = prime * result
				+ ((startDay == null) ? 0 : startDay.hashCode());
		result = prime * result
				+ ((startElevation == null) ? 0 : startElevation.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result
				+ ((stations == null) ? 0 : stations.hashCode());
		result = prime * result
				+ ((subregionTypes == null) ? 0 : subregionTypes.hashCode());
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
		Situation other = (Situation) obj;
		if (awarenessClass == null) {
			if (other.awarenessClass != null)
				return false;
		} else if (!awarenessClass.equals(other.awarenessClass))
			return false;
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
		if (awarenessType == null) {
			if (other.awarenessType != null)
				return false;
		} else if (!awarenessType.equals(other.awarenessType))
			return false;
		if (awarenessTypeCode == null) {
			if (other.awarenessTypeCode != null)
				return false;
		} else if (!awarenessTypeCode.equals(other.awarenessTypeCode))
			return false;
		if (awarenessTypeGroup == null) {
			if (other.awarenessTypeGroup != null)
				return false;
		} else if (!awarenessTypeGroup.equals(other.awarenessTypeGroup))
			return false;
		if (backgroundImage == null) {
			if (other.backgroundImage != null)
				return false;
		} else if (!backgroundImage.equals(other.backgroundImage))
			return false;
		if (backgroundImageUrl == null) {
			if (other.backgroundImageUrl != null)
				return false;
		} else if (!backgroundImageUrl.equals(other.backgroundImageUrl))
			return false;
		if (catchments == null) {
			if (other.catchments != null)
				return false;
		} else if (!catchments.equals(other.catchments))
			return false;
		if (displayAwarenessType == null) {
			if (other.displayAwarenessType != null)
				return false;
		} else if (!displayAwarenessType.equals(other.displayAwarenessType))
			return false;
		if (districts == null) {
			if (other.districts != null)
				return false;
		} else if (!districts.equals(other.districts))
			return false;
		if (endDay == null) {
			if (other.endDay != null)
				return false;
		} else if (!endDay.equals(other.endDay))
			return false;
		if (endElevation == null) {
			if (other.endElevation != null)
				return false;
		} else if (!endElevation.equals(other.endElevation))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (informationUrl == null) {
			if (other.informationUrl != null)
				return false;
		} else if (!informationUrl.equals(other.informationUrl))
			return false;
		if (nmsUrl == null) {
			if (other.nmsUrl != null)
				return false;
		} else if (!nmsUrl.equals(other.nmsUrl))
			return false;
		if (rivers == null) {
			if (other.rivers != null)
				return false;
		} else if (!rivers.equals(other.rivers))
			return false;
		if (startDay == null) {
			if (other.startDay != null)
				return false;
		} else if (!startDay.equals(other.startDay))
			return false;
		if (startElevation == null) {
			if (other.startElevation != null)
				return false;
		} else if (!startElevation.equals(other.startElevation))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (stations == null) {
			if (other.stations != null)
				return false;
		} else if (!stations.equals(other.stations))
			return false;
		if (subregionTypes == null) {
			if (other.subregionTypes != null)
				return false;
		} else if (!subregionTypes.equals(other.subregionTypes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Situation [awarenessClass=" + awarenessClass
				+ ", awarenessLevel=" + awarenessLevel
				+ ", awarenessLevelCode=" + awarenessLevelCode + ", startDay="
				+ startDay + ", endDay=" + endDay + ", awarenessTypeGroup="
				+ awarenessTypeGroup + ", awarenessType=" + awarenessType
				+ ", awarenessTypeCode=" + awarenessTypeCode
				+ ", startElevation=" + startElevation + ", endElevation="
				+ endElevation + ", subregionTypes=" + subregionTypes
				+ ", districts=" + districts + ", catchments=" + catchments
				+ ", rivers=" + rivers + ", stations=" + stations + ", nmsUrl="
				+ nmsUrl + ", informationUrl=" + informationUrl
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", displayAwarenessType=" + displayAwarenessType
				+ ", backgroundImage=" + backgroundImage
				+ ", backgroundImageUrl=" + backgroundImageUrl + "]";
	}

}
