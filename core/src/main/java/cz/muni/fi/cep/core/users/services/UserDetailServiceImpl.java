package cz.muni.fi.cep.core.users.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.muni.fi.cep.core.users.dao.CepUserDao;
import cz.muni.fi.cep.core.users.entities.CepGroupEntity;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

@Service("userDetailsService")
@Transactional
public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private CepUserDao cepUserDao;

	@Override
	public UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException {

		CepUserEntity user = cepUserDao.findByEmail(username);
		List<GrantedAuthority> authorities = buildUserAuthority(user
				.getGroups());

		return buildUserForAuthentication(user, authorities);

	}

	/**
	 * Converts {@link CepUserEntity} to {@link User}
	 *
	 * @param user
	 *            {@link CepUserEntity}
	 * @param authorities
	 *            List of {@link GrantedAuthority}
	 * @return {@link User}
	 */
	private User buildUserForAuthentication(CepUserEntity user,
			List<GrantedAuthority> authorities) {
		return new User(user.getEmail(), user.getPassword(), true, true, true,
				true, authorities);
	}

	/**
	 * Build list of authorities from groups.
	 *
	 * @param user groups
	 *            {@link CepGroupEntity}
	 * @return List of {@link GrantedAuthority}
	 */
	private List<GrantedAuthority> buildUserAuthority(
			List<CepGroupEntity> groups) {
		Set<GrantedAuthority> setAuths = new HashSet<>();

		for (CepGroupEntity group : groups) {
			setAuths.add(new SimpleGrantedAuthority(group.getCode()));
		}
		
		List<GrantedAuthority> result = new ArrayList<>(setAuths);
		return result;
	}

}
