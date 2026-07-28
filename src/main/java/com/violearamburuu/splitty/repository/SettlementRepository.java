package com.violearamburuu.splitty.repository;

import com.violearamburuu.splitty.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findAllByGroup(Group group);
    List<Settlement> findAllByFromUser(User user);
    List<Settlement> findAllByToUser(User user);
}
