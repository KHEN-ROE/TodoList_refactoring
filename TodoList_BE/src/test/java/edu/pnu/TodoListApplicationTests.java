package edu.pnu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.pnu.persistence.TodoListRepository;
import edu.pnu.persistence.UserRepository;

@SpringBootTest
class TodoListApplicationTests {

	@Autowired
	TodoListRepository tr;
	
	@Autowired
	UserRepository ur;
	
	@Test
	void contextLoads() {
	}

//	@Test
//	void insertData() {
//		User u = new User();
//		u.setEmail("aaa@aaa.com");
//		u.setLocale("ko");
//		u.setName("홍길동");
//		ur.save(u);
//		
//		for(int i=0; i<11; i++) {
//			TodoList tl = new TodoList();
//			tl.setText("test");
//			tl.setUser(u);
//			tr.save(tl);
//		}
//	}
	
}
