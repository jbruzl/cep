package cz.muni.fi.cep.core.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.activiti.engine.identity.Group;

@Entity
public class CepGroup implements Group {
	
	private static final long serialVersionUID = 5012496437616934993L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String name;
	
	private String type;

	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return Long.toString(id);
	}

	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(String id) {
		this.id = Long.parseLong(id);
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	@Override
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CepGroup [id=" + id + ", name=" + name + ", type=" + type + "]";
	}
}
