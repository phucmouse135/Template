package com.example.demo.repository;

import com.example.demo.model.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, Long> {
}
