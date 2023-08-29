package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.AppUserDto;
import com.example.ServiceApp.entity.AppUser;
import com.example.ServiceApp.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class AppUserService {

    private final PasswordEncoder appPasswordEncoder;

    private final AppUserRepository appUserRepository;

    //creeaza un user nou
    public AppUser createUser(AppUserDto appUserCreateDto) {
        AppUser user = new AppUser();
        user.setEmail(appUserCreateDto.getEmail());
        String encodedPass = appPasswordEncoder.encode(appUserCreateDto.getPass());
        user.setPass(encodedPass);
        return appUserRepository.save(user);

    }
    // folosim metoda ca sa intelegem cum functioneaza spring in spate
    public Boolean checkCredentials(AppUserDto createDto) {
        AppUser user = appUserRepository.findByEmail(createDto.getEmail());
        if(user == null) {
            return false;
        }
        return appPasswordEncoder.matches(createDto.getPass(), user.getPassword());
    }

}
