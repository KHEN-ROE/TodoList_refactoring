package edu.pnu.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserDto {
	
	@NotNull
	private String email;
	
	@NotNull
	private String name;
	
	@NotNull
	private String locale;

}
