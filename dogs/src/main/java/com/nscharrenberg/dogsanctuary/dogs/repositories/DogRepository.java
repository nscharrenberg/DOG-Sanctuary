package com.nscharrenberg.dogsanctuary.dogs.repositories;

import com.nscharrenberg.dogsanctuary.dogs.models.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
    Optional<Dog> findByName(String name);
}
