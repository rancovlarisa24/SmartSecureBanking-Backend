package com.lrs.SSB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lrs.SSB.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
