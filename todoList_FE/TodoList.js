import React, { useState, useEffect } from 'react';
import jwt_decode from 'jwt-decode';

// 데모 버전입니다. 안쓰는 컴포넌트입니다.
/* 
순서 - 0. 처음에 랜더링될 때 DB에 있는 정보를 화면에 출력하도록 만들었다.
       1. 새로운 TODO를 추가할 때 -  웹페이지에서 요청하면 DB에 먼저 데이터가 저장된다.
       2. 저장된 데이터를 다시 웹페이지에 뿌려준다.
*/

function TodoList() {
  const [todos, setTodos] = useState([]);
  const [newTodo, setNewTodo] = useState('');
  const [idToken, setIdToken] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userEmail, setUserEmail] = useState('');
  const [decodedToken, setDecodedToken] = useState('');


  useEffect(() => {
    console.log("useEffect 실행");
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token"); // 로그인에 성공하면 백엔드에서 URL에 JWT를 담아서 보냄.
    if (token) { //token이 null이 아니면
      setIdToken(token); // token 정보 저장
      console.log("토큰: ", token);
      setIsLoggedIn(true); // 로그인, 로그아웃 기능을 위해 만든 스테이트
    }
  }, []);

  // idToken 상태 변경에 대한 useEffect 추가
  useEffect(() => {
    if (idToken) {
      const decoded = jwt_decode(idToken); //토큰 디코딩해서 decoded 변수에 저장
      setDecodedToken(decoded);
      setUserEmail(decoded.sub); // 이메일 정보 추출해서 스테이트 변수에 저장. 근데 굳이 스테이트 변수로 선언할 필요 있나
      console.log("인증 토큰:", idToken);
    }
  }, [idToken]); // 로그인이 되면 idToken이 변화됨

  useEffect(() => { // get 요청. 이때는 method, headers, body 안써도 됨
    if (isLoggedIn && userEmail) {
      fetch(`/api/todos?email=${encodeURIComponent(userEmail)}`, { //Query Parameter를 설정. email이라는 파라미터에 userEmail 변수를 인코딩한 값을 설정
                                                                    // 스프링부트에서는 @RequestParam String email로 받음
                                                                    //encodeURIComponent() 함수는 문자열을 URI 인코딩하여 반환하는 함수. 
                                                                    //URI 인코딩은 URL의 일부로 사용되는 특수문자와 공백 등의 문자를 인코딩하여 안전하게 전송
        headers: {
          Authorization: `Bearer ${idToken}`, // 토큰 담아서 보냄
        },
      })
        .then((response) => response.json()) // 응답을 받아서 제이슨 형식으로 바꾸고?
        .then((data) => setTodos(data)); //받은 data를 setTodos에 파라미터로 전달
    }
  }, [isLoggedIn, userEmail, idToken]);

  //post요청으로 user 테이블에 삽입
  useEffect(() => {
    console.log("isLoggedIn:", isLoggedIn);
    console.log("userEmail:", userEmail);
    console.log("idToken:", idToken);
    console.log("decodedToken:", decodedToken);

    if (isLoggedIn && userEmail && decodedToken) {
      // 사용자 정보 추출
      const userInfo = {
        email: decodedToken.sub,
        name: decodedToken.name,
        locale: decodedToken.locale,

      };
      console.log("전송할 사용자 정보:", userInfo);

      fetch('/addUser', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${idToken}`,
        },
        body: JSON.stringify(userInfo),

      });
    }
  }, [isLoggedIn, userEmail, idToken, decodedToken]); // 사실 isLoggedIn 하나만 있어도 될 거 같은데



  function handleSubmit(e) {
    console.log("newtodo", newTodo)
    e.preventDefault();
    fetch('/api/todos', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${idToken}`,
      },
      body: JSON.stringify({ text: newTodo, user: { email: userEmail } })
    })
      .then(response => response.json())
      .then(data => { // 서버 측에서 entity 만들 때 선언했던 id, text를 포함하여 newTodo 객체를 반환한다. 
        setTodos([...todos, data]); //기존 데이터 유지하면서 새 데이터 저장
        setNewTodo('');
      });
  }

  // 엑시오스 사용한 코드
  //   import axios from 'axios';

  // function handleSubmit(e) {
  //   console.log("newtodo", newTodo)
  //   e.preventDefault();
  //   axios.post('http://localhost:8080/api/todos', {
  //     text: newTodo
  //   })
  //     .then(response => {
  //       setTodos([...todos, response.data]); 
  //       setNewTodo('');
  //     })
  //     .catch(error => {
  //       console.error(error);
  //     });
  // }

  const deleteTodo = (id) => {
    fetch(`/api/todos/${id}?email=${encodeURIComponent(userEmail)}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    })
      .then(response => {
        console.log("response", response);
        const updatedTodos = todos.filter(todo => todo.id !== id);
        setTodos(updatedTodos);
      });
  }

  // const deleteTodo = (id) => {
  //   fetch(`http://localhost:8080/api/todos/${id}`, {
  //     method: 'DELETE'
  //   })
  //     .then(response => response.json()) // 서버로부터 받은 제이슨형식의 데이터를 자.스 객체로 파싱
  // 제이슨으로 파싱 안 하면 리스폰스 객체는 문자열 형식
  // fetch 함수가 응답 데이터를 받아와서 JavaScript 객체로 자동 변환해주기 때문에,
  // 따로 response.json()과 같은 변환 작업을 하지 않아도 됨.
  // 왜냐하면, delete 요청을 보내서 db에 삭제하는 것과 웹페이지에서 삭제를 하는 것은 별개의 작업임.
  // 데이터를 응답 받아서 웹페이지에서 처리하는 것이 아님.
  // 반면 POST 방식은, 서버에서 새로운 데이터를 생성하고 생성된 데이터를 다시 클라이언트에게 반환해줌. 
  // 그렇기 때문에 클라이언트에서는 생성된 데이터를 받아와야 함.
  // 그래서 위에 create하는 함수에서는 fetch 함수의 응답을 JSON 형식으로 파싱하여 생성된 데이터를 가져오고, 
  // 그 데이터를 기존의 todos 배열에 추가한 후 상태를 업데이트하는 작업을 수행함.
  //     .then(() => {
  //       const updatedTodos = todos.filter(todo => todo.id !== id);
  //       setTodos(updatedTodos);
  //     });
  // }

  const editTodo = (id) => {
    console.log("id to edit:", id)
    const updatedText = prompt("Enter updated text:");
    if (updatedText) {
      fetch(`/api/todos/${id}?email=${encodeURIComponent(userEmail)}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${idToken}`,
        },
        body: JSON.stringify({ text: updatedText })
      })
        .then(response => response.json())
        .then(data => {
          const updatedTodos = todos.map(todo => {
            if (todo.id === id) {
              return { ...todo, text: data.text };
            } else {
              return todo;
            }
          });
          setTodos(updatedTodos);
        });
    }
  }

  return (
    <div>
      {isLoggedIn ? (
        // 로그인한 사용자에게만 보이는 컴포넌트
        <>
          <form onSubmit={handleSubmit}>
            <input type="text" value={newTodo} onChange={e => setNewTodo(e.target.value)} />
            <button type="submit">Add Todo</button>
          </form>
          <div>
            {todos.map(todo => (
              <li key={todo.id}>{todo.text}
                <button type='submit' onClick={() => editTodo(todo.id)}>edit</button>
                <button type='submit' onClick={() => deleteTodo(todo.id)} >delete</button>
              </li>
            ))}
          </div>
          {/* 로그아웃 버튼 추가 */}
          <button onClick={() => {
            window.location.href = 'http://localhost:8080/logout';
          }}>Logout</button>
        </>
      ) : (
        // 로그인하지 않은 사용자에게 보이는 컴포넌트
        <div>
          <button onClick={() => {
            window.location.href = 'http://localhost:8080/oauth2/authorization/google';
          }}>Login with Google
          </button>
        </div>
      )}
    </div>
  );
}

export default TodoList;
