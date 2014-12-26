package cz.muni.fi.cep.core.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.activiti.engine.identity.User;

@Entity
public class CepUser implements User {

	private static final long serialVersionUID = -3456927035872908174L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String mail;
	
	private String firstName;
	
	private String lastName;
	
	private String password;
	
	private String phoneNumber;
	
	@Override
	public String getEmail() {
		return mail;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getId() {
		return Long.toString(id);
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setEmail(String email) {
		this.mail = email;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public void setId(String id) {
		this.id = Long.parseLong(id);
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;

	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	

	public String getPhoneNumber() {
		return phoneNumber;
	}

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
	public String toString() {
		return "CepUser [id=" + id + ", mail=" + mail + ", firstName="
				+ firstName + ", lastName=" + lastName + ", password="
				+ password + ", phoneNumber=" + phoneNumber + "]";
	}

}
