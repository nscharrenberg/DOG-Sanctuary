package com.nscharrenberg.dogsanctuary.auth;

import com.nscharrenberg.dogsanctuary.auth.model.AppUser;
import com.nscharrenberg.dogsanctuary.auth.repositories.UserRepository;
import com.nscharrenberg.dogsanctuary.auth.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthApplication {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	@EventListener
	public void seed(ContextRefreshedEvent event) {
		try {
			createAdmin("admin", "admin@admin.com", "password", "Admin User");
		} catch (DataIntegrityViolationException e) {
			System.out.println("Error whiel seeding Admin - Probably already exists");
		}
	}

	private AppUser createAdmin(String username, String email, String password, String name) {
		AppUser user = new AppUser();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		user.setName(name);
		user.setRole("ADMIN");

		return userRepository.save(user);
	}
}
