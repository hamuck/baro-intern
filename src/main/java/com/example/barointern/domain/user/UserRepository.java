package com.example.barointern.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	default User findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(
			() -> new RuntimeException("유저를 찾을 수 없습니다.")
		);
	}

	default User findByUsernameOrElseThrow(String username){
		return findByUsername(username).orElseThrow(
			()-> new RuntimeException(("유저를 찾을 수 없습니다."))
		);
	}

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);
}
