package com.shopme.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.StringJoiner;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(unique = true, nullable = false, length = 45)
    String username;

    @Column(nullable = false, length = 64)
    String password;

    @Column(nullable = false, length = 45, name = "first_name")
    String firstName;

    @Column(nullable = false, length = 45, name = "last_name")
    String lastName;

    @Column(unique = true, nullable = false, length = 128)
    String email;

    @Column(length = 128)
    String avatar;

    boolean enabled;

    @ManyToMany
            @JoinTable(name = "user_role",
                    joinColumns = @JoinColumn(name = "user_id"),
                    inverseJoinColumns = @JoinColumn(name = "role_name")
            )
    Set<Role> roles;

    @Transient
    public String buildRole(){
        StringJoiner stringJoiner = new StringJoiner(" ");
        roles.forEach(
                role -> stringJoiner.add(role.getName())
        );
        return stringJoiner.toString();
    }

    @Transient
    public String getAvatarPath(){
        if(id == null || avatar == null) return "/api/images/user-default.png";
        return "/api/upload-avatar/" + this.id + "/" + this.avatar;
    }

}
