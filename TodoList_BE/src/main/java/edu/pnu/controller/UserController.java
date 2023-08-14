package edu.pnu.controller;



import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.dto.CreateUserDto;
import edu.pnu.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	
	private final UserService us;
	
	@PostMapping("/addUser")
	public void addUser(@RequestBody @Valid CreateUserDto createUserDto){
		us.addUser(createUserDto);
	}
	
}
