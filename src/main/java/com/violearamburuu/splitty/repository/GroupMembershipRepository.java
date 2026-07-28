package com.violearamburuu.splitty.repository;

import com.violearamburuu.splitty.model.*;

import java.lang.reflect.Member;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    List<User> findAllMemberFromGroup(Group group);
    Optional<GroupRole> findRoleOfUser(User user);

}
