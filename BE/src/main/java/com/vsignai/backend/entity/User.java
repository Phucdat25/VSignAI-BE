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
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private String password;

    private String role; // USER, ADMIN

    private Boolean isActive = true;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<UserSubscription> subscriptions;

    //thay cho createAt trong service
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}