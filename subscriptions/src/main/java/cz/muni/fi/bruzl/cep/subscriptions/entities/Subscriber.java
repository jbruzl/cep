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
public class Subscriber {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public long id;

	/**
	 * 
	 */
	public Subscriber() {
		super();
	}

	/**
	 * @param id
	 */
	public Subscriber(long id) {
		super();
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subscriber other = (Subscriber) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Subscriber [id=" + id + "]";
	}
	
	
}
