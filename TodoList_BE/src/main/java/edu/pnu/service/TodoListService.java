package edu.pnu.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pnu.domain.TodoList;
import edu.pnu.domain.User;
import edu.pnu.dto.TodoListDto;
import edu.pnu.persistence.TodoListRepository;
import edu.pnu.persistence.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoListService {
	
	private final TodoListRepository tr;

	private final UserRepository ur;
	
	public List<TodoListDto> getLists(String email){
		User findUser = ur.findByEmail(email).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저"));
		
		List<TodoList> findList = tr.findByUser(findUser);
		
		List<TodoListDto> collect = findList.stream()
		.map(t -> new TodoListDto(t.getText(), t.getUser().getEmail()))
		.toList();
		
		return collect;
		
	}
	
	public void addList(TodoListDto todoListDto) {
		//Optional 객체는 값이 존재할 수도 있고, 없을 수도 있다. 값이 있으면 .get()으로 반환
		User findUser = ur.findByEmail(todoListDto.getEmail()).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저")); // UserRepository를 사용하여 email로 User를 조회.
		
		TodoList todoList = TodoList.addList(todoListDto, findUser);
		
		tr.save(todoList);
		
	}
	
	public void updateList(TodoListDto todoListDto, Long id) {
		
		User findUser = ur.findByEmail(todoListDto.getEmail()).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저"));
		
		if(findUser.getEmail().equals(todoListDto.getEmail())) {
			TodoList todoList = tr.findById(id).orElseThrow(() -> new IllegalStateException("존재하지 않는 글"));
			todoList.setText(todoListDto.getText());
		} else {
			throw new IllegalStateException("권한 없음");
		}
		
		
	}
	
	public void deleteList(TodoListDto todoListDto, Long id) {
		User findUser = ur.findByEmail(todoListDto.getEmail()).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저"));
		
		if(findUser.getEmail().equals(todoListDto.getEmail())) {
			tr.deleteById(id);
		} else {
			throw new IllegalStateException("권한 없음");
		}
	}
}
