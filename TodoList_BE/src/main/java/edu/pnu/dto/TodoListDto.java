package edu.pnu.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TodoListDto {

	@NotNull
	private String email;
	
	@NotNull
	private String text;
	
	
}
