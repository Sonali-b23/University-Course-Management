package in.at.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import in.at.main.dao.UserDao;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		in.at.main.entity.User user = userDao.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));

		UserBuilder builder = User.withUsername(user.getUsername())
				.password(user.getPassword())
				.authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

		return builder.build();
	}
}
