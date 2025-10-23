package com.app.ecom;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/") //Used for repitition of this url
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        //return ResponseEntity.ok(userService.fetchAllUsers());
//Both ways are correct
        return new ResponseEntity<>(userService.fetchAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
//        User user = userService.fetchUser(id);
//        if(user == null)
//            return ResponseEntity.notFound().build();
//        return ResponseEntity.ok(user);

//OR below code using Streams

        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
      userService.addUser(user);
        return ResponseEntity.ok( "User Added Successfully");
    }

    @PutMapping("/{id}")
    //these two questions should come to your mind while designing an api
    //which user you want to update?-->we know this with the help of unique identifier->id
    //second what do you want to update?
    public ResponseEntity<String> updateUser(@PathVariable Long id,
                                             @RequestBody User updatedUser) {
        boolean updated = userService.updateUser(id, updatedUser);
        if (updated) {
            return ResponseEntity.ok( "User Updated Successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
