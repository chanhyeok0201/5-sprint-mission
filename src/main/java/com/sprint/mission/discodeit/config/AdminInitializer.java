package com.sprint.mission.discodeit.config;

import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		// ADMIN 권한을 가진 사용자가 있는지 확인
		boolean adminExists = userRepository.existsByRole(Role.ADMIN);

		if (!adminExists) {
			log.info("어드민 계정이 존재하지 않습니다. 어드민 계정을 생성합니다.");

			// 어드민 계정 생성
			String encodedPassword = passwordEncoder.encode("admin1234");  // 기본 비밀번호
			User admin = new User("admin", "admin@discodeit.com", encodedPassword, Role.ADMIN, null);

			userRepository.save(admin);

			log.info("어드민 계정 생성 완료: username=admin, email=admin@discodeit.com");
			log.warn("보안을 위해 어드민 비밀번호를 변경하세요!");
		} else {
			log.debug("어드민 계정이 이미 존재합니다.");
		}
	}
}
