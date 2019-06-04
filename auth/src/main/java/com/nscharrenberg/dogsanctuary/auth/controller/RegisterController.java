package com.nscharrenberg.dogsanctuary.auth.controller;

import com.nscharrenberg.dogsanctuary.auth.model.AppUser;
import com.nscharrenberg.dogsanctuary.auth.repositories.UserRepository;
import com.nscharrenberg.dogsanctuary.auth.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class RegisterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@Valid @RequestBody AppUser user) {
        AppUser created = userService.register(user);
        return new ResponseEntity<AppUser>(created,HttpStatus.CREATED);
    }
}
