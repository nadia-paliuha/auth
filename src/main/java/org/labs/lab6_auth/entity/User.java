package org.labs.lab6_auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private boolean activated = false;
    private String roles = "ROLE_USER";
    private int failedAttempts = 0;
    private Instant lockUntil;
    private String totpSecret;
    private String oauthProvider;
    private Instant createdAt = Instant.now();
}