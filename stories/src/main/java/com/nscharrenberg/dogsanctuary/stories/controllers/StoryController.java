package com.nscharrenberg.dogsanctuary.stories.controllers;

import com.nscharrenberg.dogsanctuary.stories.models.Story;
import com.nscharrenberg.dogsanctuary.stories.repositories.StoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/stories")
public class StoryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoryController.class);

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<Object> all() {
        List<Story> dogs = storyRepository.findAll();

        return new ResponseEntity<>(dogs, HttpStatus.OK);
    }

    @GetMapping("/dog/{name}")
    public ResponseEntity<Object> all(@PathVariable final String name) {
        List<Story> stories = storyRepository.findAllByDogsIn(name);

        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> all(@PathVariable final long id) {
        Optional<Story> dog = storyRepository.findById(id);

        return new ResponseEntity<>(dog, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody Story story) {

        if(story.getDogs().isEmpty()) {
            return new ResponseEntity<>("Can not create a story without a dog",HttpStatus.CREATED);
        }

        ResponseEntity error = null;

        for (String d : story.getDogs()) {
            ResponseEntity<String> dog = restTemplate.getForEntity(String.format("http://dog-service/dogs/name/%s", d), String.class);

            if (dog.getBody().equals("null") || dog.getStatusCode() != HttpStatus.OK) {
                error = new ResponseEntity<>(String.format("Dog with name %s couldn't be found in our system", d), dog.getStatusCode());
                break;
            }
        }

        if (error != null) {
            return new ResponseEntity<>(error.getBody(), HttpStatus.NOT_FOUND);
        }

        Story created = storyRepository.save(story);
        return new ResponseEntity<>(created,HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable final long id, @Valid @RequestBody Story story) {
        if(story.getId() != id) {
            return new ResponseEntity<>("Id in the path is not the same as the id given in the body", HttpStatus.NOT_ACCEPTABLE);
        }

        Optional<Story> found = storyRepository.findById(id);

        if(!found.isPresent()) {
            return new ResponseEntity<>("Story not found", HttpStatus.NOT_FOUND);
        }

        Story updated = storyRepository.save(story);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
