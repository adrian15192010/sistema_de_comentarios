package com.microservice.authservice.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenInvalidRepository extends JpaRepository<TokenInvalid, Long> {

   Optional<TokenInvalid> findByToken(String token);

}
