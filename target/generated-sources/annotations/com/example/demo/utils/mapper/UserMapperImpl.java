package com.example.demo.utils.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.model.request.UserCreateRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-22T07:21:23+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Microsoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toEntity(UserCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder<?, ?> userEntity = UserEntity.builder();

        userEntity.email( request.getEmail() );
        userEntity.username( request.getUsername() );
        userEntity.avatarUrl( request.getAvatarUrl() );
        userEntity.provider( request.getProvider() );
        userEntity.providerId( request.getProviderId() );

        return userEntity.build();
    }

    @Override
    public UserDto toDto(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.email( user.getEmail() );
        userDto.username( user.getUsername() );
        userDto.avatarUrl( user.getAvatarUrl() );
        userDto.provider( user.getProvider() );
        userDto.providerId( user.getProviderId() );
        userDto.createdAt( user.getCreatedAt() );
        userDto.updatedAt( user.getUpdatedAt() );
        userDto.deletedAt( user.getDeletedAt() );

        return userDto.build();
    }
}
