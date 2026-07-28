package com.violearamburuu.splitty.repository;

import com.violearamburuu.splitty.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findAllByGroup(Group group);
    Optional<Settlement> findAllByFromUser(User user);
}
