package com.ecommerceapi.ecommerceapi.controller;

import com.ecommerceapi.ecommerceapi.dto.UserDTO;
import com.ecommerceapi.ecommerceapi.dto.UserListDTO;
import com.ecommerceapi.ecommerceapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRegistrationController {

    private final UserService userService;

    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/details")
    @Operation(
            summary = "User details",
            description = "User details service order for a user",
            tags = {"User Service"}
    )
    public ResponseEntity<UserListDTO> getUsersDetails() {
        UserListDTO userListDTO = userService.getUsersDetails();
        return ResponseEntity.ok(userListDTO);
    }

    @GetMapping("/user/details/{userId}")
    @Operation(
            summary = "User details by user id",
            description = "User details service order for a user",
            tags = {"User Service"}
    )
    public ResponseEntity<UserDTO> getUsersDetails(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUsersDetailsById(userId));
    }

    @PostMapping("/user/registration")
    @Operation(
            summary = "User registration",
            description = "User registration",
            tags = {"User Service"}
    )
    public ResponseEntity<UserDTO> userRegistration(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.userRegistration(userDTO));
    }

    @GetMapping(value = "/user/login", params = {"userName", "password"})
    @Operation(
            summary = "User Login",
            description = "user Login",
            tags = {"User Service"}
    )
    public ResponseEntity<String> userLogin(@RequestParam String userName,
                             @RequestParam String password) {
        userService.login(userName, password);
        return ResponseEntity.ok("User login successful");
    }


}
