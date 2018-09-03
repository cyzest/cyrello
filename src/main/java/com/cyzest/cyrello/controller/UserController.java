package com.cyzest.cyrello.controller;

import com.cyzest.cyrello.dto.ApiResponse;
import com.cyzest.cyrello.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api")
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse registerUser(
            @RequestParam String id, @RequestParam String password) throws Exception {

        userService.registerUser(id, password);

        return new ApiResponse(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase());
    }

}
