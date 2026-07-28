package com.violearamburuu.splitty.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_memberships")
@NoArgsConstructor
public class GroupMembership {
    @Id
    @GeneratedValue
    private @Getter long id;
    @ManyToOne
    private @Getter @Setter User user;
    @ManyToOne
    private @Getter @Setter Group group;
    @Enumerated(EnumType.STRING)
    private @Getter @Setter GroupRole role;

    public GroupMembership(User user, Group group, GroupRole role){
        this.user = user;
        this.group = group;
        this.role = role;
    }
}
