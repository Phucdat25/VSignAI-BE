package com.vsignai.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<UserSubscription> subscriptions;

    //thay cho createAt trong service
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, BAN
    }
    public enum Role {
        USER, ADMIN
    }
}