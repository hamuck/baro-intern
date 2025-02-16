package com.example.barointern.global.Security;

import com.example.barointern.domain.user.User;
import com.example.barointern.domain.user.UserRepository;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailService(UserRepository userRepository){
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		User user = userRepository.findByUsernameOrElseThrow(username);

		return new org.springframework.security.core.userdetails.User(
			user.getUsername(),
			user.getPassword(),
			Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().toString()))
		);
	}
}
