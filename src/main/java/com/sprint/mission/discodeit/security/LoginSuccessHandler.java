package com.sprint.mission.discodeit.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		// 1. 인증된 사용자 정보 가져오기
		DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
		UserDto userDto = userDetails.getUserDto();

		log.info("로그인 성공: userId={}, username={}", userDto.id(), userDto.username());

		// 2. JSON 응답 설정
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// 3. UserDto를 JSON으로 변환해서 응답
		String jsonResponse = objectMapper.writeValueAsString(userDto);
		response.getWriter().write(jsonResponse);
	}
}
