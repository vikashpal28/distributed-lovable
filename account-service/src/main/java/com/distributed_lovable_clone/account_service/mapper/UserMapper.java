package com.distributed_lovable_clone.account_service.mapper;


import com.distributed_lovable_clone.account_service.dto.auth.SignUpRequest;
import com.distributed_lovable_clone.account_service.dto.auth.UserProfileResponse;
import com.distributed_lovable_clone.account_service.entity.User;
import com.distributed_lovable_clone.common_lib.dto.UserDto;
import com.distributed_lovable_clone.common_lib.security.JwtUserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "userId" , target = "id")
    UserProfileResponse toUserProfileResponse(JwtUserPrincipal user);

    User toEntity(SignUpRequest signUpRequest);

    UserDto toUserDto(User user);
}
