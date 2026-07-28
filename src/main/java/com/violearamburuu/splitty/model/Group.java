package com.violearamburuu.splitty.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "groups")
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue
    private @Getter long id;
    private @Getter
    @Setter String name;

    public Group(String name) {
        this.name = name;
    }
}