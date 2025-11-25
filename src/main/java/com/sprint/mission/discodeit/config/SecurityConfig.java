package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.sprint.mission.discodeit.security.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.security.CustomAuthenticationEntryPoint;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity  // Method Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final CustomAccessDeniedHandler accessDeniedHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {

		http
			.csrf(csrf -> csrf
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
			)
			.authorizeHttpRequests(auth -> auth
				// Static 리소스 허용 (프론트엔드)
				.requestMatchers("/", "/index.html").permitAll()
				.requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
				.requestMatchers("/favicon.ico", "/favicon.*", "/*.ico", "/*.png").permitAll()

				// API - 인증 없이 접근 가능
				.requestMatchers("/api/auth/csrf-token").permitAll()  // CSRF 토큰 발급
				.requestMatchers("/api/users").permitAll()  // 회원가입 (POST)
				.requestMatchers("/api/auth/login").permitAll()  // 로그인
				.requestMatchers("/api/auth/logout").permitAll()  // 로그아웃

				// API가 아닌 요청 (Swagger, Actuator 등)
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()

				// 나머지 모든 요청은 인증 필요
				.anyRequest().authenticated()
			)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint(authenticationEntryPoint)  // 401
				.accessDeniedHandler(accessDeniedHandler)  // 403
			)
			.formLogin(login -> login
				.loginProcessingUrl("/api/auth/login")
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)
			)
			.logout(logout -> logout
				.logoutUrl("/api/auth/logout")
				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
			)
			.rememberMe(rememberMe -> rememberMe
				.key("discodeit-remember-me-key")  // RememberMe 쿠키 암호화 키
				.tokenValiditySeconds(60 * 60 * 24 * 14)  // 14일 (2주)
				.rememberMeParameter("remember-me")  // 요청 파라미터 이름
				.rememberMeCookieName("remember-me")  // 쿠키 이름
			)
			.sessionManagement(management -> management
				.sessionConcurrency(concurrency -> concurrency
					.maximumSessions(1)  // 동시 세션 1개만 허용
					.maxSessionsPreventsLogin(false)  // 새 로그인 시 기존 세션 만료
					.sessionRegistry(sessionRegistry)  // SessionRegistry 등록
				)
			);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		// ADMIN > CHANNEL_MANAGER > USER
		roleHierarchy.setHierarchy(
			"ROLE_ADMIN > ROLE_CHANNEL_MANAGER\n" +
				"ROLE_CHANNEL_MANAGER > ROLE_USER"
		);
		return roleHierarchy;
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

}