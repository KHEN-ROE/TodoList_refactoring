import React, { useEffect, useState } from 'react'
import axios from 'axios';

const Tform = () => {
  const [todos, setTodos] = useState([]); // 입력값을 담을 배열
  const [newTodo, setNewTodo] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userEmail, setUserEmail] = useState('');


  useEffect(() => {
      setIsLoggedIn(true)
  }, []);

  useEffect(() => {
  if (isLoggedIn) {
    axios.get('/loginSuccess')
      .then((response) => {
        setUserEmail(response.data);
        return axios.get('/api/todos');
      })
      .then((response) => {
        setTodos(response.data);
      });
  }
}, [isLoggedIn]);


  const addTodo = (e) => {
  e.preventDefault();
  if(newTodo.trim() !== ''){
    axios.post('/api/todos', { text: newTodo, email: userEmail })
      .then(response => {
        setTodos([...todos, response.data]);
        setNewTodo('');
      });
  }
}

  const deleteTodo = (id) => {
  axios.delete(`/api/todos/${id}`, { data: { email: userEmail } })
    .then(() => {
      const updatedTodos = todos.filter(todo => todo.id !== id);
      setTodos(updatedTodos);
    });
}

  const editTodo = (id) => {
  const updatedText = prompt("수정할 텍스트 입력:");
  if (updatedText) {
    axios.put(`/api/todos/${id}`, { text: updatedText, email: userEmail })
      .then(response => {
        const updatedTodos = todos.map(todo => {
          if (todo.id === id) {
            return { ...todo, text: response.data.text };
          } else {
            return todo;
          }
        });
        setTodos(updatedTodos);
      });
  }
}
  
  return (
    <div className='form'>
      {isLoggedIn ? (
        // 로그인한 사용자에게만 보이는 컴포넌트
        <div>
          <form onSubmit={addTodo}>
            <input type="text" value={newTodo} onChange={e => setNewTodo(e.target.value)} placeholder='                                                                    Stop Everything, Do Onething' />
            <button type="submit">Add Todo</button>
            {/* 로그아웃 버튼 추가 */}
            <button onClick={() => { window.location.href = 'http://localhost:8080/logout'; }}>Logout</button>
          </form>
          <div className='list'>
            {todos.map(todo => (
              <li key={todo.id}>{todo.text}
                <button type='submit' onClick={() => editTodo(todo.id)}>edit</button>
                <button type='submit' onClick={() => deleteTodo(todo.id)} >delete</button>
              </li>
            ))}
          </div>

        </div>
      ) : (
        // 로그인하지 않은 사용자에게 보이는 컴포넌트
        <div className='logbutton'>
          <button onClick={() => { window.location.href = 'http://localhost:8080/oauth2/authorization/google'; }}>Login with Google</button>     
        </div>
      )}
    </div>
  );
}

export default Tform
