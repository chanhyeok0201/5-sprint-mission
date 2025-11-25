package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Role;

public record UserDto(
    UUID id,
    String username,
    String email,
	Role role,
    BinaryContentDto profile,
    Boolean online
) {

}
