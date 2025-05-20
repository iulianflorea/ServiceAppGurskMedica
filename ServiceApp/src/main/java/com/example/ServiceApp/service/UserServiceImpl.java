package com.example.ServiceApp.service;


import com.example.ServiceApp.dto.UserDto;
import com.example.ServiceApp.entity.User;
import com.example.ServiceApp.mapper.UserMapper;
import com.example.ServiceApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
@Override
    public List<UserDto> findAll() {
        List<User> userList = userRepository.findAll();
        return userMapper.toDtoList(userList);
    }

    @Override
    public UserDto update(UserDto userDto) {
        User userToBeUpdated = userRepository.findById(userDto.getId()).orElseThrow();
        userToBeUpdated.setFirstname(userDto.getFirstname());
        userToBeUpdated.setLastname(userDto.getLastname());
        userToBeUpdated.setEmail(userDto.getEmail());
        userToBeUpdated.setPassword(userDto.getPassword());
        User userSaved = userRepository.save(userToBeUpdated);
        return userMapper.toDto(userSaved);
    }
    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }


}
