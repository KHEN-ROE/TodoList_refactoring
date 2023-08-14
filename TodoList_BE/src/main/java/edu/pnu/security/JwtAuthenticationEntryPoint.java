package edu.pnu.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component // 이 클래스를 스프링 컨테이너에 Bean으로 등록
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	// 인증 실패 시 실행되는 메서드
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"); // 클라이언트에게 401 Unauthorized 오류를 전송
    }
}
