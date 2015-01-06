package cz.muni.fi.cep.core.users.services;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.identity.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

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
		
		user.setId(cepUserDao.save(user).getId());
		logger.info("User {} created", user);
	}

	@Override
	public void deleteUser(CepUserEntity user) {
		if (user == null)
			throw new NullPointerException("User shouldn't be null.");

		cepUserDao.delete(user);
		logger.info("User {} deleted", user);
	}

	@Override
	public void updateUser(CepUserEntity user) {
		createUser(user);
		logger.info("User {} updated", user);
	}

	@Override
	public CepUserEntity getCepUserById(Long id) {
		if (id == null)
			throw new NullPointerException("Id shouldn't be null.");

		CepUserEntity cepUserEntity = cepUserDao.findOne(id);
		logger.info("Returning user {}", cepUserEntity);
		return cepUserEntity;
	}

	@Override
	public List<CepUserEntity> getAllCepUsers() {
		List<CepUserEntity> cepUserEntities = new ArrayList<CepUserEntity>();

		for (CepUserEntity entity : cepUserDao.findAll()) {
			cepUserEntities.add(entity);
		}
		logger.info("Returning list of users. Size: {}", cepUserEntities.size());
		return cepUserEntities;
	}

	@Override
	public void createGroup(CepGroupEntity cepGroupEntity) {
		if (cepGroupEntity == null)
			throw new NullPointerException(
					"IdentityService: CepGroupEntity is null.");
		cepGroupEntity.setId(cepGroupDao.save(cepGroupEntity).getId());
		logger.info("Group {} created", cepGroupEntity);
	}

	@Override
	public void deleteGroup(CepGroupEntity cepGroupEntity) {
		if (cepGroupEntity == null)
			throw new NullPointerException(
					"IdentityService: CepGroupEntity is null.");
		cepGroupDao.delete(cepGroupEntity);
		logger.info("Group {} deleted", cepGroupEntity);
	}

	@Override
	public void updateGroup(CepGroupEntity cepGroupEntity) {
		if (cepGroupEntity == null)
			throw new NullPointerException(
					"IdentityService: CepGroupEntity is null.");
		createGroup(cepGroupEntity);
		logger.info("Group {} updated", cepGroupEntity);
	}

	@Override
	public CepGroupEntity getGroupById(Long id) {
		if (id == null)
			throw new NullPointerException("Id shouldn't be null.");
		
		CepGroupEntity group = cepGroupDao.findOne(id);
		logger.info("Returning group {}", group);
		return group;
	}

	@Override
	public List<CepGroupEntity> getAllGroups() {
		List<CepGroupEntity> cepGroupEntities = new ArrayList<CepGroupEntity>();

		for (CepGroupEntity entity : cepGroupDao.findAll()) {
			cepGroupEntities.add(entity);
		}
		logger.info("Returning list of groups. Size: {}", cepGroupEntities.size());
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

		if (cepUserDao.findOne(Long.parseLong(cepUserEntity.getId())) == null) {
			throw new IllegalArgumentException("Given " + cepUserEntity
					+ " does not exists.");
		}

		if (cepGroupDao.findOne(Long.parseLong(cepGroupEntity.getId())) == null) {
			throw new IllegalArgumentException("Given " + cepGroupEntity
					+ " does not exists.");
		}

		cepUserEntity.getGroups().add(cepGroupEntity);
		cepGroupEntity.getUsers().add(cepUserEntity);

		updateGroup(cepGroupEntity);
		updateUser(cepUserEntity);
		logger.info("Created membership of {} in {}", cepUserEntity, cepGroupEntity);
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
		logger.info("Membership of {} in {} deleted", cepUserEntity, cepGroupEntity);
	}

	@Override
	public void setAuthenticatedUserId(String authenticatedUserId) {
		Authentication.setAuthenticatedUserId(authenticatedUserId);
		logger.info("Setting authentication to {}", authenticatedUserId);
	}
}
