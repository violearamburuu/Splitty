package com.violearamburuu.splitty.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    private @Getter @Setter String name;
    @Id
    @GeneratedValue
    private @Getter long id;
    @Column(unique = true)
    private @Getter @Setter String email;
    private @Getter @Setter String passwordHash;

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.passwordHash = password;
    }
}
