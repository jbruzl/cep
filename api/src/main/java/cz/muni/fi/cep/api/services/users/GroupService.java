package cz.muni.fi.cep.api.services.users;

import java.util.List;

import cz.muni.fi.cep.api.DTO.CepGroup;

/**
 * Interface for group service, that manages all groups.
 * 
 * @author Jan Bruzl
 *
 */
public interface GroupService {

	/**
	 * Creates new group.
	 * 
	 * @param cepGroupEntity {@link CepGroup}
	 */
	public void createGroup(CepGroup cepGroupEntity);
	
	/**
	 * Deletes group.
	 * 
	 * @param cepGroupEntity {@link CepGroup}
	 */
	public void deleteGroup(CepGroup CepGroup);
	
	/**
	 * Updates group.
	 * 
	 * @param cepGroupEntity {@link CepGroup}
	 */
	public void updateGroup(CepGroup CepGroup);
	
	/**
	 * Gets group with given id.
	 * 
	 * @param id {@link Long}
	 * @return {@link CepGroup}
	 */
	public CepGroup getGroupById(Long id);
	
	/**
	 * Returns list of all groups.
	 * 
	 * @return {@link List<CepGroup>}
	 */
	public List<CepGroup> getAllGroups();

	/**
	 * Gets group with given code.
	 * 
	 * @param code
	 * @return {@link CepGroup}
	 */
	public abstract CepGroup getGroupByCode(String code);
	
}
