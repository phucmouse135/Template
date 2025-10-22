package com.example.demo.repository;

import com.example.demo.model.entity.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseRepository<RoleEntity, String> {}
