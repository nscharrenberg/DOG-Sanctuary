package com.nscharrenberg.dogsanctuary.auth.services;

import com.nscharrenberg.dogsanctuary.auth.model.AppUser;
import com.nscharrenberg.dogsanctuary.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    public AppUser register(AppUser user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }
}
