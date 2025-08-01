package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.UserDto;
import com.example.ServiceApp.entity.User;

import java.util.List;

public interface UserService {


    List<UserDto> findAll();

    UserDto update(UserDto userDto);

    void delete(Long id);

    User findByEmail(String email);
}
