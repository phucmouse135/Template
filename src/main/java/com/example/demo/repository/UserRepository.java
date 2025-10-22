package com.example.demo.repository;

import com.example.demo.model.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, Long> {
    // existsByUsername
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.username = ?1")
    boolean existsByUsername(String username);


    // findByUsername
    @EntityGraph(attributePaths = {"roles"})
    Optional<UserEntity> findByUsername(String username);
}
