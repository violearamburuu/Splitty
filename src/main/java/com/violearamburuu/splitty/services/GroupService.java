package com.violearamburuu.splitty.services;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.GroupMembershipRepository;
import com.violearamburuu.splitty.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    public GroupService (GroupRepository groupRepository, GroupMembershipRepository groupMembershipRepository){
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
    }

    public Group createGroup(String name, User owner){
        Group group = new Group(name);
        GroupMembership membership = new GroupMembership(owner, group, GroupRole.OWNER);

        groupRepository.save(group);
        groupMembershipRepository.save(membership);

        return group;
    }

    public Group findGroupById(long id){
        return groupRepository.findById(id).orElseThrow(() -> new RuntimeException("This group does not exist."));
    }

    public Group findGroupByName(String name){
        return groupRepository.findByName(name).orElseThrow(() -> new RuntimeException("This group doesn't exist"));
    }

    public List<User> getAllGroupMembers(Group group){
        List<GroupMembership> memberships = groupMembershipRepository.findByGroup(group);
        List<User> members = new ArrayList<User>();
        for (GroupMembership membership : memberships){
            members.add(membership.getUser());
        }
        return members;
    }

    public GroupMembership addMemberToGroup(User currentUser, User addedMember, Group group){
        GroupMembership membership = groupMembershipRepository.findByUserAndGroup(currentUser, group).orElseThrow(() -> new RuntimeException("This user doesn't belong to given group"));
        if (membership.getRole() != GroupRole.OWNER) throw new RuntimeException("This user isn't the group's owner");
        if (groupMembershipRepository.findByUserAndGroup(addedMember, group).isPresent()) throw new RuntimeException("This user already belongs to this group.");

        GroupMembership newMembership = new GroupMembership(addedMember, group, GroupRole.MEMEBR);
        groupMembershipRepository.save(newMembership);
        return newMembership;
    }

    public void removeMemberFromGroup(User currentUser, User member, Group group){
        GroupMembership currentUserMembership = groupMembershipRepository.findByUserAndGroup(currentUser, group).orElseThrow(() -> new RuntimeException("This user doesn't belong to given group"));
        if (currentUserMembership.getRole() != GroupRole.OWNER) throw new RuntimeException("This user isn't the group's owner");
        GroupMembership userMembership = groupMembershipRepository.findByUserAndGroup(member, group).orElseThrow(() -> new RuntimeException("This user does not belong to this group."));
        groupMembershipRepository.delete(userMembership);
    }

    public GroupRole getUserRole(User user, Group group){
        GroupMembership membership = groupMembershipRepository.findByUserAndGroup(user, group).orElseThrow(() -> new RuntimeException("This user does not belong to this group."));
        return membership.getRole();
    }
}
