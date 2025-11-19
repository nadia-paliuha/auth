package org.labs.lab6_auth.repository;

import org.labs.lab6_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByActivationToken(String token);

    @Query("select u from User u where u.username = :id or u.email = :id")
    Optional<User> findByUsernameOrEmail(@Param("id") String username, @Param("id") String email);

    boolean existsByEmail(String finalEmail);
}
