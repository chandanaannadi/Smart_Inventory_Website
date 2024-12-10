package com.example.inventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorySystem.entity.User;
import com.example.inventorySystem.liveChat.user.Status;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
    
    User findByEmail(String email);
    User findByRememberMeToken(String rememberMeToken);
    User findBySessionToken(String sessionToken);
    //User findByResetToken(String sessionToken);

    List<User> findAllByStatus(Status status);
    
    List<User> findByRole(String role);
    
    List<User> findByRoleAndActive(String role, Boolean active);
}
