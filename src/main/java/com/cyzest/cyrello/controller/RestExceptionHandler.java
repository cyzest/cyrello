package com.cyzest.cyrello.controller;

import io.github.cyzest.commons.spring.web.AbstractApiExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Component
@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler extends AbstractApiExceptionHandler {

}
