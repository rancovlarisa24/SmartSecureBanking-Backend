package com.lrs.SSB.repository;

import com.lrs.SSB.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilityRepository extends JpaRepository<Utility, Long> {
}
