package cz.muni.fi.cep.core.users.api;

import java.util.List;

import cz.muni.fi.cep.core.users.entities.CepGroupEntity;

public interface GroupService {

	/**
	 * Creates new group.
	 * 
	 * @param cepGroupEntity {@link CepGroupEntity}
	 */
	public void createGroup(CepGroupEntity cepGroupEntity);
	
	/**
	 * Deletes group.
	 * 
	 * @param cepGroupEntity {@link CepGroupEntity}
	 */
	public void deleteGroup(CepGroupEntity cepGroupEntity);
	
	/**
	 * Updates group.
	 * 
	 * @param cepGroupEntity {@link CepGroupEntity}
	 */
	public void updateGroup(CepGroupEntity cepGroupEntity);
	
	/**
	 * Gets group with given id.
	 * 
	 * @param id {@link Long}
	 * @return {@link CepGroupEntity}
	 */
	public CepGroupEntity getGroupById(Long id);
	
	/**
	 * Returns list of all groups.
	 * 
	 * @return {@link List<CepGroupEntity>}
	 */
	public List<CepGroupEntity> getAllGroups();
	
}
