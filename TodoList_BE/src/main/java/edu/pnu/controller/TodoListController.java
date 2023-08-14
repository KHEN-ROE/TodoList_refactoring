package edu.pnu.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.TodoList;
import edu.pnu.dto.TodoListDto;
import edu.pnu.service.TodoListService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class TodoListController {

	private final TodoListService ts;
	
	@GetMapping("/api/todos")
	public List<TodoListDto> getLists(@RequestParam @Valid String email) { // 쿼리파라미터를 @RequestParam으로 받음
		return ts.getLists(email);
	}
	
	@PostMapping("/api/todos")
	public void addList(@RequestBody @Valid TodoListDto TodoListDto) { //post 방식일 때는 @RequestBody 있어야 클라이언트의 요청을 바인딩할 수 있다.
																//message converter가 requestbody를 todolist 타입의 객체로 변환시켜줌
																//즉, JSON을 객체형태로 변환
		ts.addList(TodoListDto);
	}
	
	@PutMapping("/api/todos/{id}")
	public void updateList(@RequestBody @Valid TodoListDto TodoListDto, @PathVariable Long id){ //클라이언트에서 json형식으로 요청 바디에 담아서 put요청,
																	// 여기서 바디를 추출하기 위해 @RequestBody를 쓰는거임
		ts.updateList(TodoListDto, id);
	}
	
	@DeleteMapping("/api/todos/{id}")
	public void deleteList(@RequestBody @Valid TodoListDto TodoListDto, @PathVariable Long id) {
		ts.deleteList(TodoListDto, id);
	}
	
}
