package com.mate.controller;

import com.mate.model.dto.UserDto;
import com.mate.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/user")
@Log4j2
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            return ResponseEntity.ok(userService.registerUser(userDto).getUserId());
        } catch (Exception e) {
            String errMsg = String.format("Error creating user=%s with error=%s", userDto.getName(), e.getMessage());
            log.error(errMsg);
            return ResponseEntity.internalServerError().body(errMsg);
        }
    }

//    @PostMapping("/login")
//    public UserDto loginUser(@RequestBody UserLoginRequest userLoginRequest) {
//
//    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            String errorMsg = "Error getting all users" + e.getMessage();
            log.error(errorMsg);
            return ResponseEntity.internalServerError().body(errorMsg);
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("Successfully Deleted user=" + userId);
        } catch (Exception e) {
            String errMsg = "Error deleting all users" + e.getMessage();
            log.error(errMsg);
            return ResponseEntity.internalServerError().body(errMsg);
        }
    }
}
