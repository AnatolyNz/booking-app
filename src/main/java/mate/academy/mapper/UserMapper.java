package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserRegistrationRequestDto requestDto);
}
