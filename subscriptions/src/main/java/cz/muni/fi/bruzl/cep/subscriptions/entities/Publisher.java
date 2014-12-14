/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Jan Bruzl
 *
 */
@Entity
public class Publisher {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String code;

	/**
	 * 
	 */
	public Publisher() {
	}

	/**
	 * @param id
	 * @param code
	 */
	public Publisher(long id, String code) {
		super();
		this.id = id;
		this.code = code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Publisher [id=" + id + ", code=" + code + "]";
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

}
