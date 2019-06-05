package com.nscharrenberg.dogsanctuary.stories.controllers;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.ribbon.proxy.annotation.Http;
import com.nscharrenberg.dogsanctuary.stories.models.Story;
import com.nscharrenberg.dogsanctuary.stories.repositories.StoryRepository;
import com.nscharrenberg.dogsanctuary.stories.utils.RestTemplateResponseErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/stories")
public class StoryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoryController.class);

    @Autowired
    private StoryRepository storyRepository;

    private RestTemplate restTemplate;

    @Autowired
    public StoryController(RestTemplate restTemplate, RestTemplateResponseErrorHandler restTemplateResponseErrorHandler) {
        this.restTemplate = restTemplate;
        this.restTemplate.setErrorHandler(restTemplateResponseErrorHandler);
    }

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
        LOGGER.info("Creating a Story ...");
        if(story.getDogs().isEmpty()) {
            return new ResponseEntity<>("Can not create a story without a dog",HttpStatus.CREATED);
        }

        for (String d : story.getDogs()) {
            LOGGER.info("Starting the Search for the dog in this story ...");
            ResponseEntity<Object> dog = null;
            try {
                dog = restTemplate.getForEntity("http://dog-service/dogs/name/{dog}", Object.class, d);
            } catch (HttpClientErrorException e) {
                dog = new ResponseEntity<Object>(e.getResponseBodyAsString(), e.getStatusCode());
            }

            if (dog.getStatusCode() != HttpStatus.OK) {
                LOGGER.info(String.format("%s - %s", dog.getStatusCode(), dog.getBody().toString()));
                return dog;
            }
        }

        Story created = storyRepository.save(story);
        LOGGER.info("Story has been created");
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
