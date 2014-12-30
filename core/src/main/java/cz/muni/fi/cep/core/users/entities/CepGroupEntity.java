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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		CepGroupEntity other = (CepGroupEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CepGroupEntity [id=" + id + ", code=" + code + ", name=" + name
				+ ", type=" + type + ", users=" + users + "]";
	}

}
