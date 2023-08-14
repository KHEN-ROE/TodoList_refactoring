package edu.pnu.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pnu.domain.User;
import edu.pnu.dto.CreateUserDto;
import edu.pnu.persistence.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

	private final UserRepository userRepository;


	public void addUser(CreateUserDto createUserDto) {
		
		userRepository.findByEmail(createUserDto.getEmail()).ifPresent(user -> {
			throw new IllegalStateException("이미 존재하는 회원");
		});
		
		User user = User.createUser(createUserDto);
		
		userRepository.save(user);
	}
}
