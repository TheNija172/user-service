package org.example.userservice.repository;

import org.example.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    void updateActive(Long id, Boolean active);
    
}