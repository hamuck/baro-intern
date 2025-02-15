package com.example.barointern.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	default User findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(
			() -> new RuntimeException("유저를 찾을 수 없습니다.")
		);
	}
}
