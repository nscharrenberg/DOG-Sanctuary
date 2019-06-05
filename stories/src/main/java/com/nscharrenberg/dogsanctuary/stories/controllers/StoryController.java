package com.nscharrenberg.dogsanctuary.stories.controllers;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Http;
import com.nscharrenberg.dogsanctuary.stories.models.Story;
import com.nscharrenberg.dogsanctuary.stories.repositories.StoryRepository;
import com.nscharrenberg.dogsanctuary.stories.services.DogService;
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
    private DogService dogService;

    @GetMapping
    public ResponseEntity<Object> all() {
        List<Story> dogs = storyRepository.findAll();

        return new ResponseEntity<>(dogs, HttpStatus.OK);
    }

    @GetMapping("/dog/{name}")
    public ResponseEntity<Object> all(@PathVariable final String name) {
        List<Story> stories = storyRepository.findAllByDogsInOrderByHappenedAtDescCreatedAtDesc(name);

        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> all(@PathVariable final long id) {
        Optional<Story> dog = storyRepository.findById(id);

        return new ResponseEntity<>(dog, HttpStatus.OK);
    }

    @HystrixCommand(fallbackMethod = "fallback")
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody Story story) {

        if(story.getDogs().isEmpty()) {
            return new ResponseEntity<>("Can not create a story without a dog",HttpStatus.CREATED);
        }

        for (String d : story.getDogs()) {
            ResponseEntity<Object> dog = dogService.dogExists(d);

            if (dog.getStatusCode() != HttpStatus.OK) {
                return dog;
            }
        }

        Story created = storyRepository.save(story);
        return new ResponseEntity<>(created,HttpStatus.CREATED);
    }

    public ResponseEntity<Object> fallback(Story story) {
        return new ResponseEntity<>("Dog Service could not be reached",HttpStatus.SERVICE_UNAVAILABLE);
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
