package com.app.ecom;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private List<User> userList = new ArrayList<>();
    private Long nextId = 1L;

    public List<User> fetchAllUsers() {
        return userList;
    }

    public void addUser(User user) {
        user.setId(nextId++);
        userList.add(user);
    }

    public User fetchUser(Long id) {
        for(User user : userList) {
            if(user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}
