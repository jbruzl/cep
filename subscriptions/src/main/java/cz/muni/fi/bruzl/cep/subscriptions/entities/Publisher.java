/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * @author Jan Bruzl
 *
 */
@Entity
public class Publisher {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(unique=true, nullable=false)
	private String code;
	
	@OneToMany(mappedBy="publisher", fetch=FetchType.LAZY)
	private List<Subscriber> subscribers = new ArrayList<Subscriber>();

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

	/**
	 * @return the subscribers
	 */
	public List<Subscriber> getSubscribers() {
		return subscribers;
	}

	/**
	 * @param subscribers the subscribers to set
	 */
	public void setSubscribers(List<Subscriber> subscribers) {
		this.subscribers = subscribers;
	}
	

}
