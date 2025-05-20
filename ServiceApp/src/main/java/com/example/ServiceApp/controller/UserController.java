package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.UserDto;
import com.example.ServiceApp.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/findAll")
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PutMapping("/update")
    public UserDto update(@RequestBody UserDto userDto) {
        return userService.update(userDto);
    }

    @DeleteMapping("/deleteById")
    public void delete(@RequestParam Long id) {
        userService.delete(id);
    }


}
