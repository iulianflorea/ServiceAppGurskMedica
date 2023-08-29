package com.example.ServiceApp.controller;


import com.example.ServiceApp.dto.AppUserDto;
import com.example.ServiceApp.entity.AppUser;
import com.example.ServiceApp.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@AllArgsConstructor
@RestController
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping("/register")
    public AppUser registerUser(@RequestBody AppUserDto createDto){
        return appUserService.createUser(createDto);
    }
    @PostMapping("/login")

    public ResponseEntity<?> loginUser(@RequestBody AppUserDto createDto){
        if (appUserService.checkCredentials(createDto)){
            return ResponseEntity.ok(createDto);
        }else{
            return  ResponseEntity.badRequest().body("Credentiale incorecte!");
        }

    }
}
