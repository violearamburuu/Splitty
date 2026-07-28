package com.violearamburuu.splitty;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.GroupRepository;
import com.violearamburuu.splitty.repository.GroupMembershipRepository;
import com.violearamburuu.splitty.services.GroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock private GroupRepository groupRepository;
    @Mock private GroupMembershipRepository groupMembershipRepository;

    @InjectMocks private GroupService groupService;

    @Test
    void createGroup_makesCreatorOwner() {
        User ana = new User("Ana", "ana@test.com", "hash");
        when(groupRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        groupService.createGroup("Trip", ana);

        verify(groupRepository).save(any());                  // group saved
        verify(groupMembershipRepository).save(any());        // owner membership saved
    }

    @Test
    void addMember_ownerAddsNewMember_succeeds() {
        User ana = new User("Ana", "ana@test.com", "hash");   // owner
        User beto = new User("Beto", "beto@test.com", "hash"); // new member
        Group group = new Group("Trip");

        when(groupMembershipRepository.findByUserAndGroup(ana, group))
                .thenReturn(Optional.of(new GroupMembership(ana, group, GroupRole.OWNER)));
        when(groupMembershipRepository.findByUserAndGroup(beto, group))
                .thenReturn(Optional.empty());                    // not already a member

        groupService.addMemberToGroup(ana, beto, group);

        verify(groupMembershipRepository).save(any());        // new membership saved
    }

    @Test
    void addMember_callerNotOwner_throwsAndSavesNothing() {
        User ana = new User("Ana", "ana@test.com", "hash");   // only a MEMBER
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        when(groupMembershipRepository.findByUserAndGroup(ana, group))
                .thenReturn(Optional.of(new GroupMembership(ana, group, GroupRole.MEMEBR)));

        assertThrows(RuntimeException.class, () ->
                groupService.addMemberToGroup(ana, beto, group));

        verify(groupMembershipRepository, never()).save(any());
    }

    @Test
    void addMember_callerNotInGroup_throws() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        when(groupMembershipRepository.findByUserAndGroup(ana, group))
                .thenReturn(Optional.empty());                    // caller isn't a member at all

        assertThrows(RuntimeException.class, () ->
                groupService.addMemberToGroup(ana, beto, group));

        verify(groupMembershipRepository, never()).save(any());
    }

    @Test
    void addMember_alreadyMember_throwsAndSavesNothing() {
        User ana = new User("Ana", "ana@test.com", "hash");   // owner
        User beto = new User("Beto", "beto@test.com", "hash"); // already in group
        Group group = new Group("Trip");

        when(groupMembershipRepository.findByUserAndGroup(ana, group))
                .thenReturn(Optional.of(new GroupMembership(ana, group, GroupRole.OWNER)));
        when(groupMembershipRepository.findByUserAndGroup(beto, group))
                .thenReturn(Optional.of(new GroupMembership(beto, group, GroupRole.MEMEBR)));

        assertThrows(RuntimeException.class, () ->
                groupService.addMemberToGroup(ana, beto, group));

        verify(groupMembershipRepository, never()).save(any());
    }
}