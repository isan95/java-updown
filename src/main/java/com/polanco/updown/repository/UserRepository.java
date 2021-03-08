package com.polanco.updown.repository;

import java.util.Optional;

import com.polanco.updown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);
    
    Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}
