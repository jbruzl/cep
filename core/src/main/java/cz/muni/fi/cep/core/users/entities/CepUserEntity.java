package cz.muni.fi.cep.core.users.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.activiti.engine.identity.User;

@Entity
public class CepUserEntity implements User {

	private static final long serialVersionUID = -3456929935872908174L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String phoneNumber;

	private String firstName;
	private String lastName;
	private String password;
	private String email;
	
	@ManyToMany(fetch=FetchType.LAZY)
	private List<CepGroupEntity> groups = new ArrayList<>();

	public List<CepGroupEntity> getGroups() {
		return groups;
	}

	public void setGroups(List<CepGroupEntity> groups) {
		this.groups = groups;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getId() {
		return Long.toString(id);
	}

	@Override
	public void setId(String id) {
		this.id = Long.parseLong(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		CepUserEntity other = (CepUserEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CepUserEntity [id=" + id + ", phoneNumber=" + phoneNumber
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", password=" + password + ", email=" + email + ", groups="
				+ groups + "]";
	}

}
