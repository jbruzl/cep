package cz.muni.fi.cep.core.users.services;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.identity.Authentication;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.services.users.IdentityService;
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

	@Autowired
	private Mapper mapper;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public void setCepUserDao(CepUserDao cepUserDao) {
		this.cepUserDao = cepUserDao;
	}

	public void setCepGroupDao(CepGroupDao cepGroupDao) {
		this.cepGroupDao = cepGroupDao;
	}

	@Override
	public void createUser(CepUser user) {
		if (user == null)
			throw new NullPointerException("User shouldn't be null.");

		CepUserEntity userEntity = mapper.map(user, CepUserEntity.class);

		String id = cepUserDao.save(userEntity).getId();
		if (id == null) {
			logger.warn("User {} not persisted.", user);
		}
		user.setId(Long.parseLong(id));
		logger.info("User {} created", user);
	}

	@Override
	public void deleteUser(CepUser user) {
		if (user == null)
			throw new NullPointerException("User shouldn't be null.");

		CepUserEntity daoUser = cepUserDao.findOne(user.getId());
		List<Long> groups = new ArrayList<>();
		for (CepGroupEntity entity : daoUser.getGroups()) {
			groups.add(Long.parseLong(entity.getId()));
		}
		for (Long id : groups) {
			deleteMembership(user, getGroupById(id));
		}
		if (daoUser.getGroups().size() != 0) {
			logger.error(
					"Could not delete {} because still contains users: {}",
					daoUser, daoUser.getGroups());
		}
		cepUserDao.delete(daoUser);

		logger.info("User {} deleted", daoUser);
	}

	@Override
	public void updateUser(CepUser user) {
		if (user == null)
			throw new NullPointerException("User shouldn't be null.");

		CepUserEntity userEntity = cepUserDao.findOne(user.getId());

		mapper.map(user, userEntity);

		String id = cepUserDao.save(userEntity).getId();
		if (id == null) {
			logger.warn("User {} not persisted.", user);
		}
		user.setId(Long.parseLong(id));
		logger.info("User {} updated", user);
	}

	@Override
	public CepUser getCepUserById(Long id) {
		if (id == null)
			throw new NullPointerException("Id shouldn't be null.");

		CepUserEntity cepUserEntity = cepUserDao.findOne(id);
		logger.info("Returning user {}", cepUserEntity);
		return mapper.map(cepUserEntity, CepUser.class);
	}
	
	@Override
	public CepUser getCepUserByEmail(String email) {
		if (email == null)
			throw new NullPointerException("Email shouldn't be null.");
		
		if (email.length() == 0)
			throw new NullPointerException("Email shouldn't be empty.");

		CepUserEntity cepUserEntity = cepUserDao.findByEmail(email);
		logger.info("Returning user {}", cepUserEntity);
		return mapper.map(cepUserEntity, CepUser.class);
	}

	@Override
	public List<CepUser> getAllCepUsers() {
		List<CepUser> cepUsers = new ArrayList<>();

		for (CepUserEntity entity : cepUserDao.findAll()) {
			cepUsers.add(mapper.map(entity, CepUser.class));
		}
		logger.info("Returning list of users. Size: {}", cepUsers.size());
		return cepUsers;
	}

	@Override
	public void createGroup(CepGroup cepGroup) {
		if (cepGroup == null)
			throw new NullPointerException("CepGroup is null.");

		CepGroupEntity cepGroupEntity = mapper.map(cepGroup,
				CepGroupEntity.class);
		cepGroupEntity.setId(cepGroupDao.save(cepGroupEntity).getId());
		logger.info("Group {} created", cepGroupEntity);
	}

	@Override
	public void deleteGroup(CepGroup cepGroup) {
		if (cepGroup == null)
			throw new NullPointerException("CepGroupEntity is null.");
		CepGroupEntity group = cepGroupDao.findOne(cepGroup.getId());

		List<Long> users = new ArrayList<>();
		for (CepUserEntity entity : group.getUsers()) {
			users.add(Long.parseLong(entity.getId()));
		}
		for (Long id : users) {
			deleteMembership(getCepUserById(id), cepGroup);
		}
		if (group.getUsers().size() != 0) {
			logger.error(
					"Could not delete {} because still contains users: {}",
					group, users);
		}
		cepGroupDao.delete(group);
		logger.info("Group {} deleted", cepGroup);
	}

	@Override
	public void updateGroup(CepGroup cepGroup) {
		if (cepGroup == null)
			throw new NullPointerException("CepGroupEntity is null.");

		CepGroupEntity cepGroupEntity = cepGroupDao.findOne(cepGroup.getId());
		mapper.map(cepGroup, cepGroupEntity);
		
		String id = cepGroupDao.save(cepGroupEntity).getId();
		cepGroup.setId(Long.parseLong(id));
	
		logger.info("Group {} updated", cepGroupEntity);
	}

	@Override
	public CepGroup getGroupById(Long id) {
		if (id == null)
			throw new NullPointerException("Id shouldn't be null.");

		CepGroupEntity group = cepGroupDao.findOne(id);
		logger.info("Returning group {}", group);
		return mapper.map(group, CepGroup.class);
	}
	

	@Override
	public CepGroup getGroupByCode(String code) {
		if (code == null)
			throw new NullPointerException("Id shouldn't be null.");

		CepGroupEntity group = cepGroupDao.findByCode(code);
		if(group == null)
			return null;
		logger.info("Returning group {}", group);
		return mapper.map(group, CepGroup.class);
	}

	@Override
	public List<CepGroup> getAllGroups() {
		List<CepGroup> cepGroup = new ArrayList<>();

		for (CepGroupEntity entity : cepGroupDao.findAll()) {
			cepGroup.add(mapper.map(entity, CepGroup.class));
		}
		logger.info("Returning list of groups. Size: {}",
				cepGroup.size());
		return cepGroup;
	}

	@Override
	public void createMembership(CepUser cepUser, CepGroup cepGroup) {
		if (cepGroup == null)
			throw new NullPointerException(
					"CepGroup is null.");
		if (cepUser == null)
			throw new NullPointerException("User shouldn't be null.");

		CepUserEntity cepUserEntity = cepUserDao.findOne(cepUser.getId());
		if (cepUserEntity == null) {
			throw new IllegalArgumentException("Given " + cepUser
					+ " does not exists.");
		}

		CepGroupEntity cepGroupEntity = cepGroupDao.findOne(cepGroup.getId());
		if (cepGroupEntity == null) {
			throw new IllegalArgumentException("Given " + cepGroup
					+ " does not exists.");
		}

		List<CepGroupEntity> groups = cepUserEntity.getGroups();
		if (!groups.contains(cepGroupEntity))
			groups.add(cepGroupEntity);
		
		List<CepUserEntity> users = cepGroupEntity.getUsers();
		if (!users.contains(cepUserEntity))
			users.add(cepUserEntity);

		cepUserDao.save(cepUserEntity);
		cepGroupDao.save(cepGroupEntity);
		
		logger.info("Created membership of {} in {}", cepUser,
				cepGroup);
	}

	@Override
	public void deleteMembership(CepUser cepUser, CepGroup cepGroup) {
		if (cepGroup == null)
			throw new NullPointerException(
					"CepGroup is null.");
		if (cepUser == null)
			throw new NullPointerException("User shouldn't be null.");

		CepUserEntity cepUserEntity = cepUserDao.findOne(cepUser.getId());
		if (cepUserEntity == null) {
			throw new IllegalArgumentException("Given " + cepUser
					+ " does not exists.");
		}

		CepGroupEntity cepGroupEntity = cepGroupDao.findOne(cepGroup.getId());
		if (cepGroupEntity == null) {
			throw new IllegalArgumentException("Given " + cepGroup
					+ " does not exists.");
		}

		List<CepGroupEntity> groups = cepUserEntity.getGroups();
		if (groups.contains(cepGroupEntity))
			groups.remove(cepGroupEntity);
		List<CepUserEntity> users = cepGroupEntity.getUsers();
		if (users.contains(cepUserEntity))
			users.remove(cepUserEntity);

		cepUserDao.save(cepUserEntity);
		cepGroupDao.save(cepGroupEntity);
		
		logger.info("Membership of {} in {} deleted", cepUserEntity,
				cepGroupEntity);
	}
	
	@Override
	public List<CepUser> getMembers(CepGroup cepGroup){
		if(cepGroup == null) {
			logger.error("CepGroup is null");
			return null;
		}
			
		List<CepUser> users = new ArrayList<>();
		CepGroupEntity cepGroupEntity = cepGroupDao.findOne(cepGroup.getId());
		if(cepGroupEntity==null) {
			logger.error("CepUser {} not found", cepGroup);
			return null;
		}
		for(CepUserEntity cue : cepGroupEntity.getUsers()) {
			users.add(mapper.map(cue, CepUser.class));			
		}
		return users;
	}
	
	@Override
	public List<CepGroup> getMemberships(CepUser cepUser){
		if(cepUser == null) {
			logger.error("CepUser is null");
			return null;
		}
			
		List<CepGroup> groups = new ArrayList<>();
		CepUserEntity cepUserEntity = cepUserDao.findOne(cepUser.getId());
		if(cepUserEntity==null) {
			logger.error("CepUser {} not found", cepUser);
			return null;
		}
		for(CepGroupEntity cge : cepUserEntity.getGroups()) {
			groups.add(mapper.map(cge, CepGroup.class));			
		}
		return groups;
	}

	@Override
	public void setAuthenticatedUserId(String authenticatedUserId) {
		Authentication.setAuthenticatedUserId(authenticatedUserId);
		logger.info("Setting authentication to {}", authenticatedUserId);
	}
}
