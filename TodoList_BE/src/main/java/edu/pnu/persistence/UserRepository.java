package edu.pnu.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
		Optional<User> findByEmail(String email);
}
