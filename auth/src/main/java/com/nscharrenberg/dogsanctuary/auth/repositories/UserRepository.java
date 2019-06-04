package com.nscharrenberg.dogsanctuary.auth.repositories;

import com.nscharrenberg.dogsanctuary.auth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Integer> {
}
