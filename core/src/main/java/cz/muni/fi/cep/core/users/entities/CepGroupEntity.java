package cz.muni.fi.cep.core.users.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.activiti.engine.identity.Group;

@Entity
public class CepGroupEntity implements Group {
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<CepUserEntity> getUsers() {
		return users;
	}

	public void setUsers(List<CepUserEntity> users) {
		this.users = users;
	}

	private static final long serialVersionUID = 7994771811850733591L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique = true, nullable = false)
	private String code;
	private String name;
	private String type;
	
	@ManyToMany(mappedBy="groups", fetch=FetchType.LAZY)
	private List<CepUserEntity> users = new ArrayList<>();

	@Override
	public String getId() {
		return Long.toString(id);
	}

	@Override
	public void setId(String id) {
		this.id = Long.parseLong(id);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

}
