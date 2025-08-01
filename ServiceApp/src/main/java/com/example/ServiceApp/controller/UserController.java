package com.example.ServiceApp.controller;//package com.example.ServiceApp.controller;
//
//import com.example.ServiceApp.dto.UserDto;
//import com.example.ServiceApp.service.UserService;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/user")
//public class UserController {
//
//    public final UserService userService;
//
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping("/findAll")
//    public List<UserDto> findAll() {
//        return userService.findAll();
//    }
//
//    @PutMapping("/update")
//    public UserDto update(@RequestBody UserDto userDto) {
//        return userService.update(userDto);
//    }
//
//    @DeleteMapping("/deleteById")
//    public void delete(@RequestParam Long id) {
//        userService.delete(id);
//    }
//
//
//}

import com.example.ServiceApp.config.JwtService;
import com.example.ServiceApp.dto.UserDto;
import com.example.ServiceApp.entity.User;
import com.example.ServiceApp.mapper.UserMapper;
import com.example.ServiceApp.repository.UserRepository;
import com.example.ServiceApp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserService userService, JwtService jwtService, UserRepository userRepository, UserMapper userMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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

    @GetMapping("/current-role")
    public String getCurrentUserRole(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getRole().name();
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userService.findByEmail(email);
        return userMapper.toDto(user); // dacă ai un mapper, altfel construiești manual DTO-ul
    }
}

