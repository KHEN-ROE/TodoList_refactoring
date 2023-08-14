package edu.pnu.domain;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.pnu.dto.TodoListDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TodoList { // 자식 테이블
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Setter
	private String text;
	
	@ManyToOne
	@JoinColumn(name = "email")
	private User user;
	
	public TodoList(String text, User user) {
		this.text = text;
		this.user = user;
	}
	
	public static TodoList addList(TodoListDto todoListDto, User user) {
		return new TodoList(todoListDto.getText(), user);
	}
	
}
