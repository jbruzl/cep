/**
 * 
 */
package cz.muni.fi.cep.warning.chmi.report;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Representation of CHMU weather reports Paragraph element
 * @author Jan Bruzl
 *
 */
public class Paragraph implements Serializable {
	private static final long serialVersionUID = -2563966546157154416L;
	
	private String type;
	
	private Boolean list = false;
	
	private String text;

	public String getType() {
		return type;
	}

	@XmlAttribute(name="type", required=true)
	public void setType(String type) {
		this.type = type;
	}

	public Boolean getList() {
		return list;
	}

	@XmlAttribute(name="list", required=false)
	public void setList(Boolean list) {
		this.list = list;
	}

	public String getText() {
		return text;
	}

	@XmlValue
	public void setText(String text) {
		this.text = text;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Paragraph))
			return false;
		Paragraph other = (Paragraph) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Paragraph [type=" + type + ", list=" + list + ", text=" + text
				+ "]";
	}
	
	

}
