package edu.pnu.security;


import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;


@Configuration //  Spring의 설정 클래스를 나타냄. 이 클래스를 통해 Spring Security 설정을 정의.
@EnableWebSecurity // Spring Security를 사용하도록 설정
@RequiredArgsConstructor
public class SecurityConfig {

	
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Value("${jwt.secret}")
  	private String token;
	
    @Bean
    // 스프링 시큐리티에서 요청을 처리하기 위해 필요한 필터들의 체인
    // 클라이언트의 요청이 서버에 도달할 때부터 응답이 클라이언트에게 전달될 때까지의 모든 과정에서 보안 요소를 적용
    // HttpSecurity 클래스를 통해 개발자는 원하는 보안 요소를 추가
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable().cors().and() // CSRF 보호를 비활성화하고 CORS를 활성화. 근데 cors를 disable()해도 상관은 없네.
            .authorizeRequests() // 요청에 대한 인증 권한을 설정 -- 스프링부트 자체적으로 인증 및 인가를 거치는 과정
                .antMatchers("/", "/oauth2/**").permitAll() // 루트 경로와 /oauth2 경로는 인증없이 모두 접근 가능
                .antMatchers("/api/todos").authenticated()// 반면에 /api/todos 경로는 인증된 사용자만 접근 가능
                .anyRequest().authenticated() // 나머지 요청은 모두 인증된 사용자만 접근 가능
            .and()
            .oauth2Login() // 시큐리티가 OAuth2.0 로그인 과정 처리하도록 설정 - 이게 맨 처음 와야되는 것 같은데
                .defaultSuccessUrl("/loginSuccess", true) // 로그인 성공시 이동할 URL을 설정
                										// 이 때, 스프링 시큐리티는 구글 인증 서버로부터 받은 인증 코드를 이용해
                										// 구글 OAuth 2.0 인증 서버에 액세스 토큰을 요청하고 얻어서 사용자정보 얻어옴
                										// 클라이언트 ID와 시크릿이 사용됨
                										// 스프링 시큐리티가 이 과정을 자동으로 처리
                .failureUrl("/loginFailure") // 로그인 실패시 이동할 URL을 설정
            .and()
            .logout() // 로그아웃 설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // 로그아웃 요청에 대한 경로 설정 
                															//AntPathRequestMatcher는 요청 경로와 주어진 패턴이 일치하는지 확인하는 데 사용되는 클래스
                .logoutSuccessUrl("http://localhost:3000") // 로그아웃 후 리다이렉트할 URL을 설정
                .invalidateHttpSession(true) // 로그아웃 시 HTTP 세션을 무효화
                .deleteCookies("JSESSIONID"); // 로그아웃 시 JSESSIONID 쿠키를 삭제
        									//JSESSIONID는 Java 웹 애플리케이션에서 사용되는 세션 ID의 이름
        									// SESSIONID는 주로 쿠키를 통해 클라이언트에 저장되며, 
        									// 클라이언트가 요청을 보낼 때마다 JSESSIONID 쿠키를 함께 전송해 서버에 자신을 식별
        
        http.oauth2ResourceServer() // 리액트에서 토큰을 전달했을 때 여기서 받는다
        .jwt()
        .decoder(jwtDecoder()) // jwtDecoder()를 이용해 토큰을 디코딩
        .jwtAuthenticationConverter(jwtAuthenticationConverter()); // jwtAuthenticationConverter()를 사용하여 JWT 토큰의 정보를 Authentication 객체로 변환
        															// 이 변환을 통해 스프링 시큐리티는 사용자 인증 및 인가 과정을 수행할 수 있음
        															// 이 과정이 끝나면 인증이 완료된 것임. 이제 요청을 처리하고 리액트에 응답 전송
        http.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint); // 인증 예외 처리 설정
        return http.build();
    }
    
    @Bean
    JwtDecoder jwtDecoder() {
    	// 비밀 키 생성
        SecretKey secretKey = Keys.hmacShaKeyFor(token.getBytes(StandardCharsets.UTF_8));
        
        // 비밀 키를 이용한 JWT 디코더를 생성
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).build();
        
        // 발행자 검증을 위한 토큰 검증기를 생성
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("https://accounts.google.com");
        
        // audience 검증을 위한 토큰 검증기를 생성.
        // 이 익명 클래스는 withAudience 변수에 할당되어 OAuth2TokenValidator<Jwt> 인스턴스로 사용됨. 
        // 이렇게 익명 클래스를 사용하면, 간단한 구현이 필요한 경우에 클래스 선언 없이 코드를 간결하게 작성할 수 있음
        OAuth2TokenValidator<Jwt> withAudience = new OAuth2TokenValidator<Jwt>() { // 익명클래스 사용하여 OAuth2TokenValidator<Jwt> 인터페이스 구현
        																		// 익명 클래스는 클래스의 선언과 동시에 객체를 생성하는 방법
        	//JWT 토큰의 "aud" claim을 검증하는 로직을 작성
        	@Override
            public OAuth2TokenValidatorResult validate(Jwt jwt) {
        		// JWT 토큰에서 "aud"라는 claim을 가져와서 문자열 리스트로 저장
                List<String> audList = jwt.getClaim("aud");

		@Value("${spring.security.oauth2.client.registration.google.client-id}")    
                String expectedAud;
		    
                if (audList != null && audList.contains(expectedAud)) {
                    return OAuth2TokenValidatorResult.success(); // audience가 유효하면 검증 결과를 성공으로 반환
                } else {
                 // audience가 유효하지 않으면 검증 결과를 실패로 반환
                    return OAuth2TokenValidatorResult.failure(new OAuth2Error("유효하지 않은 aud", "aud claim이 유효하지 않습니다", null));
                }
            }
        };
        
        // 발행자와 audience 검증기를 결합한 검증기를 생성
        OAuth2TokenValidator<Jwt> combinedValidators = new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience);
        jwtDecoder.setJwtValidator(combinedValidators); // 발급자와 수신자 정보가 올바른지 확인하여 토큰의 유효성을 검사

        return jwtDecoder;
    }
    
    //JwtAuthenticationConverter는 SecurityFilterChain에서 JWT를 받아 인증 객체로 변환해주는 역할을 함. 
    //인증 및 인가 과정에서 사용됨
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() { //JwtAuthenticationConverter는 JWT 토큰을 Authentication 객체로 변환해주는 클래스
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        
        // JwtGrantedAuthoritiesConverter는 JWT 토큰의 클레임을 권한(GrantedAuthority) 객체로 변환해주는 컨버터
        // JWT 토큰의 클레임 정보를 이용해 사용자의 권한 정보를 생성하고 관리할 수 있음
        converter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthoritiesConverter()); //JwtGrantedAuthoritiesConverter를 사용하여 JWT 토큰의 
        																					//'scope' 또는 'scp' 클레임을 기반으로 인증 객체의 권한 정보를 생성
        return converter;
    }
}

