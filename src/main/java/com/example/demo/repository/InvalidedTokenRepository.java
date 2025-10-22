package com.example.demo.repository;

import com.example.demo.model.entity.InvalidatedToken;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidedTokenRepository extends BaseRepository<InvalidatedToken, String> {}
