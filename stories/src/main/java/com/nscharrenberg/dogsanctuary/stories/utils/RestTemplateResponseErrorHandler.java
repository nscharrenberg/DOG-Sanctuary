package com.nscharrenberg.dogsanctuary.stories.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

    private List acceptableStatus;

    public RestTemplateResponseErrorHandler(@Value("${good-status}") String goodStatus) {
        acceptableStatus = Arrays.stream(goodStatus.split(","))
                .map(HttpStatus::valueOf)
                .collect(Collectors.toList()) ;
    }

    /**
     * Indicate whether the given response has any errors.
     * <p>Implementations will typically inspect the
     * {@link ClientHttpResponse#getStatusCode() HttpStatus} of the response.
     *
     * @param response the response to inspect
     * @return {@code true} if the response indicates an error; {@code false} otherwise
     * @throws IOException in case of I/O errors
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return !acceptableStatus.contains(response.getStatusCode());
    }

    /**
     * Handle the error in the given response.
     * <p>This method is only called when {@link #hasError(ClientHttpResponse)}
     * has returned {@code true}.
     *
     * @param response the response with the error
     * @throws IOException in case of I/O errors
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
    }
}
