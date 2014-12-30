package cz.muni.fi.cep.core.users.services;

import java.util.ArrayList;
import java.util.List;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.muni.fi.cep.core.users.api.IdentityService;
import cz.muni.fi.cep.core.users.dao.CepGroupDao;
import cz.muni.fi.cep.core.users.dao.CepUserDao;
import cz.muni.fi.cep.core.users.entities.CepGroupEntity;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

@Service
@Transactional
public class CepIdentityService implements IdentityService {
	@Autowired
	private CepUserDao cepUserDao;

	@Autowired
	private CepGroupDao cepGroupDao;

	@Override
	public void setCepUserDao(CepUserDao cepUserDao) {
		this.cepUserDao = cepUserDao;
	}

	@Override
	public void setCepGroupDao(CepGroupDao cepGroupDao) {
		this.cepGroupDao = cepGroupDao;
	}

	@Override
	public void createUser(CepUserEntity user) {
		if (user == null)
			throw new NullPointerException("User shouldn't be null.");

		cepUserDao.save(user);
	}

	@Override
	public void deleteUser(CepUserEntity user) {
		if (user == null)
			throw new NullPointerException("User shouldn't be null.");

		cepUserDao.delete(user);
	}

	@Override
	public void updateUser(CepUserEntity user) {
		createUser(user);
	}

	@Override
	public CepUserEntity getCepUserById(Long id) {
		if (id == null)
			throw new NullPointerException("Id shouldn't be null.");

		CepUserEntity cepUserEntity = cepUserDao.findOne(id);

		return cepUserEntity;
	}

	@Override
	public List<CepUserEntity> getAllCepUsers() {
		List<CepUserEntity> cepUserEntities = new ArrayList<CepUserEntity>();

		for (CepUserEntity entity : cepUserDao.findAll()) {
			cepUserEntities.add(entity);
		}

		return cepUserEntities;
	}

	@Override
	public void createGroup(CepGroupEntity cepGroupEntity) {
		if (cepGroupEntity == null)
			throw new NullPointerException(
					"IdentityService: CepGroupEntity is null.");
		cepGroupDao.save(cepGroupEntity);
	}

	@Override
	public void deleteGroup(CepGroupEntity cepGroupEntity) {
		if (cepGroupEntity == null)
			throw new NullPointerException(
					"IdentityService: CepGroupEntity is null.");
		cepGroupDao.delete(cepGroupEntity);
	}

	@Override
	public void updateGroup(CepGroupEntity cepGroupEntity) {
		if (cepGroupEntity == null)
			throw new NullPointerException(
					"IdentityService: CepGroupEntity is null.");
		createGroup(cepGroupEntity);
	}

	@Override
	public CepGroupEntity getGroupById(Long id) {
		if (id == null)
			throw new NullPointerException("Id shouldn't be null.");
		return cepGroupDao.findOne(id);
	}

	@Override
	public List<CepGroupEntity> getAllGroups() {
		List<CepGroupEntity> cepGroupEntities = new ArrayList<CepGroupEntity>();

		for (CepGroupEntity entity : cepGroupDao.findAll()) {
			cepGroupEntities.add(entity);
		}

		return cepGroupEntities;
	}

	@Override
	public void createMembership(CepUserEntity cepUserEntity,
			CepGroupEntity cepGroupEntity) {
		if (cepGroupEntity == null)
			throw new NullPointerException(
					"IdentityService: CepGroupEntity is null.");
		if (cepUserEntity == null)
			throw new NullPointerException("User shouldn't be null.");
		
		cepUserEntity.getGroups().add(cepGroupEntity);
		cepGroupEntity.getUsers().add(cepUserEntity);
		
		updateGroup(cepGroupEntity);
		updateUser(cepUserEntity);
	}

	@Override
	public void deleteMembership(CepUserEntity cepUserEntity,
			CepGroupEntity cepGroupEntity) {
		if (cepGroupEntity == null)
			throw new NullPointerException(
					"IdentityService: CepGroupEntity is null.");
		if (cepUserEntity == null)
			throw new NullPointerException("User shouldn't be null.");
		
		cepUserEntity.getGroups().remove(cepGroupEntity);
		cepGroupEntity.getUsers().remove(cepUserEntity);
		
		updateGroup(cepGroupEntity);
		updateUser(cepUserEntity);
	}

}
