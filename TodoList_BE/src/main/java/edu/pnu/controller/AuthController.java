package edu.pnu.controller;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
public class AuthController {

	private static final String REDIRECT_URI = "http://localhost:3000";

	@Value("${jwt.secret}")
  	private String token;

	@GetMapping("/loginSuccess")
	public ResponseEntity<String> loginSuccess(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) { 
		// @AuthenticationPrincipal OAuth2User principal 파라미터는 인증된 사용자의 정보를 담고 있다

		// 인증된 사용자의 이메일을 추출
    		String userEmail = principal.getAttribute("email");
																						
		// JWT 생성
		String JWT = createJWT(principal);

		// JWT 디코딩 및 "aud" 클레임 값 출력
		SecretKey secretKey = Keys // 시크릿 키 생성 (토큰 서명에 사용)
				.hmacShaKeyFor(token.getBytes(StandardCharsets.UTF_8));
		JwtParser parser = Jwts.parserBuilder().setSigningKey(secretKey).build(); // JWT 파서 빌더에 시크릿 키를 설정하고 파서를 빌드
		Jws<Claims> jws = parser.parseClaimsJws(JWT); // JWT 파싱을 통해 JWS 객체를 얻음
		String audClaim = jws.getBody().get("aud", String.class); // JWS에서 클레임을 가져와 "aud" 클레임 값을 가져옴
		
		// JWT를 HttpOnly 쿠키에 저장
		Cookie jwtCookie = new Cookie("jwt", JWT);
	    jwtCookie.setHttpOnly(true);    // 스크립트를 통한 접근 방지
//	    jwtCookie.setSecure(true);      // HTTPS에서만 쿠키 전송
	    jwtCookie.setPath("/");         // 전체 사이트에서 쿠키 접근 가능
	    response.addCookie(jwtCookie);  // 응답에 쿠키 추가

	    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.SET_COOKIE, JWT)
                .location(URI.create(REDIRECT_URI))
                .body(userEmail);
	}

	//OAuth 2.0을 통해 얻은 사용자 정보를 기반으로 JWT 토큰을 생성
	private String createJWT(OAuth2User principal) {
		// JWT 생성 로직 구현 (예: 토큰 만료 시간, 서명 등)
		// 사용자 정보에서 필요한 부분을 가져와 토큰에 포함시킴
		// 여기서는 사용자의 이메일, 이름, 지역을 토큰의 subject로 사용.
		String email = principal.getAttribute("email");
		String name = principal.getAttribute("name");
	    String locale = principal.getAttribute("locale");
		Instant now = Instant.now(); // 현재 시간을 가져옴
		Instant expiry = now.plus(1, ChronoUnit.HOURS); // 현재 시간에 1시간을 더하여 만료 시간을 설정

		SecretKey secretKey = Keys
				.hmacShaKeyFor(token.getBytes(StandardCharsets.UTF_8));
		@Value("${spring.security.oauth2.client.registration.google.client-id}")
		final String AUDIENCE

		String jwt = Jwts.builder().setSubject(email)
				.claim("name", name) 
				.claim("locale", locale) //토큰에 이메일, 이름, 지역 추가
				.setIssuer("https://accounts.google.com").setAudience(AUDIENCE) // 클라이언트 ID를 audience로 추가																														
				.setIssuedAt(Date.from(now)).setExpiration(Date.from(expiry))
				.signWith(secretKey) // 생성된 키를 사용하여 서명
				.compact();

		return jwt;
	}

//	@GetMapping("/loginFailure")
//	public RedirectView loginFailure() {
//		redirectView.setUrl("http://localhost:3000");
//		return redirectView;
//	}
}
