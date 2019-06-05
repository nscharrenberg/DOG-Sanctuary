package com.nscharrenberg.dogsanctuary.stories.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.nscharrenberg.dogsanctuary.stories.models.Story;
import com.nscharrenberg.dogsanctuary.stories.utils.RestTemplateResponseErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DogService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Make sure the dog exists in the services
     * @param dogName
     * @return
     */
    @HystrixCommand(fallbackMethod = "fallback")
    public ResponseEntity<Object> dogExists(String dogName) {
        try {
            return restTemplate.getForEntity(String.format("http://dog-service/dogs/name/%s", dogName), Object.class);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<Object>(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }
}
