package edu.pnu.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.TodoList;
import edu.pnu.domain.User;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
	
	//인터페이스에 추가된 사용자 지정 메서드, JpaRepository를 확장한 것임
	//이 메서드를 추가함으로써 특정 User 객체와 연관된 TodoList 객체들을 조회할 수 있음
	List<TodoList> findByUser(User user); // 의미 - todoList 테이블에서 user를 기준으로 모든 데이터 조회

}
