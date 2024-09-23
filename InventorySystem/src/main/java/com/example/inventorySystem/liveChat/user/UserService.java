package com.example.inventorySystem.liveChat.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.inventorySystem.entity.User;
import com.example.inventorySystem.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User saveUser(AddUser user) {
        User user1 = repository.findByUserName(user.userName);
        user1.setStatus(Status.ONLINE);
        return repository.save(user1);
    }

    public User disconnect(AddUser user) {
        User storedUser = repository.findByUserName(user.userName);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            repository.save(storedUser);
        }
        return storedUser;
    }

    public List<User> findAllUsers() {
        return repository.findAll();
    }
    
    public List<User> findAdminUsers() {
        return repository.findByRole("ADMIN");
    }
}
