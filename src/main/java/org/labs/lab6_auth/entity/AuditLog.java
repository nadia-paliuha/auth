package org.labs.lab6_auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Entity
@Getter
@Setter
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String usernameOrEmail;
    private boolean success;
    private String ip;
    private Instant time = Instant.now();
    private String reason;
}
