package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.UserDto;

import java.util.List;

public interface UserService {


    List<UserDto> findAll();

    UserDto update(UserDto userDto);

    void delete(Long id);
}
