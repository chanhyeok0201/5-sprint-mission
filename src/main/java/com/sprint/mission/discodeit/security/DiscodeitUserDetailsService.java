package com.sprint.mission.discodeit.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info(" 사용자 조회 시작: username = {}", username);
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("사용자를 찾을 수 없음: username = {}", username);
				return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
			});
		log.info("사용자 조회 완료: userId={}, username={}", user.getId(), username);

		// 2. UserDto로 변환
		var userDto = userMapper.toDto(user);

		// 3. DiscodeitUserDetails 생성 및 반환
		return new DiscodeitUserDetails(userDto, user.getPassword());
	}

}
