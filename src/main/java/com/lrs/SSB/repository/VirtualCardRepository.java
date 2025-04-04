package com.lrs.SSB.repository;

import com.lrs.SSB.entity.VirtualCard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualCardRepository extends JpaRepository<VirtualCard, Integer> {
    List<VirtualCard> findByUserId(Integer userId);
    @Transactional
    void deleteBySourceCardId(Integer sourceCardId);
    Optional<VirtualCard> findBySourceCardId(Integer sourceCardId);
}
