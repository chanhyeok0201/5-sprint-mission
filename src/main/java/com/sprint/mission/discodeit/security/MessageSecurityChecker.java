package com.sprint.mission.discodeit.security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

// 메시지 권한 체크를 위한 Security Helper
@Component("messageSecurityChecker")
@RequiredArgsConstructor
public class MessageSecurityChecker {

	private final MessageRepository messageRepository;

	/**
	 * 현재 사용자가 메시지의 작성자인지 확인
	 *
	 * @param messageId 메시지 ID
	 * @param userId    현재 사용자 ID
	 * @return 작성자이면 true
	 */
	public boolean isOwner(UUID messageId, UUID userId) {
		return messageRepository.findById(messageId)
			.map(message -> message.getAuthor().getId().equals(userId))
			.orElse(false);
	}

}
