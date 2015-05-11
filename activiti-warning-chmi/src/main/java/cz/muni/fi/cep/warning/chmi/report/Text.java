/**
 * 
 */
package cz.muni.fi.cep.warning.chmi.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representation of CHMU weather reports Text element
 * @author Jan Bruzl
 *
 */
public class Text implements Serializable {
	private static final long serialVersionUID = -2542870423292668516L;

	private String id;
	
	private List<Paragraph> paragraphs = new ArrayList<>();

	public String getId() {
		return id;
	}

	@XmlAttribute(name="id", required=true)
	public void setId(String id) {
		this.id = id;
	}

	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}

	@XmlElement(name="paragraph", type=Paragraph.class,required=false)
	public void setParagraphs(List<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((paragraphs == null) ? 0 : paragraphs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Text))
			return false;
		Text other = (Text) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (paragraphs == null) {
			if (other.paragraphs != null)
				return false;
		} else if (!paragraphs.equals(other.paragraphs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Text [id=" + id + ", paragraphs=" + paragraphs + "]";
	}
	
	
}
