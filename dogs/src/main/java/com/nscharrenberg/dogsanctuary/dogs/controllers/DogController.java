package com.nscharrenberg.dogsanctuary.dogs.controllers;

import com.nscharrenberg.dogsanctuary.dogs.models.Dog;
import com.nscharrenberg.dogsanctuary.dogs.repositories.DogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dogs")
public class DogController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DogController.class);

    @Autowired
    private DogRepository dogRepository;

    @GetMapping
    public ResponseEntity<Object> all() {
        List<Dog> dogs = dogRepository.findAll();

        if(!dogs.isEmpty()) {
            return new ResponseEntity<>("No dogs found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dogs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable final long id) {
        Optional<Dog> dog = dogRepository.findById(id);

        if(!dog.isPresent()) {
            return new ResponseEntity<>(String.format("Dog with id %s could not be found", id), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dog, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Object> findByName(@PathVariable final String name) {
        LOGGER.info(String.format("Search a dog with the name of %s", name));
        Optional<Dog> dog = dogRepository.findByName(name);

        if(!dog.isPresent()) {
            LOGGER.info(String.format("Dog with the name of %s could not be found in our system", name));
            return new ResponseEntity<>(String.format("Dog with name %s could not be found", name), HttpStatus.NOT_FOUND);
        }

        LOGGER.info(String.format("Dog with the name of %s has been found", name));
        return new ResponseEntity<>(dog, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody Dog dog) {
        Dog created = dogRepository.save(dog);
        return new ResponseEntity<>(created,HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable final long id, @Valid @RequestBody Dog dog) {
        if(dog.getId() != id) {
            return new ResponseEntity<>("Id in the path is not the same as the id given in the body", HttpStatus.NOT_ACCEPTABLE);
        }

        Optional<Dog> found = dogRepository.findById(id);

        if(!found.isPresent()) {
            return new ResponseEntity<>("Dog not found", HttpStatus.NOT_FOUND);
        }

        Dog updated = dogRepository.save(dog);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
