package com.cyzest.cyrello.controller;

import com.cyzest.cyrello.dto.UserInfo;
import com.cyzest.cyrello.dto.UserRegParam;
import com.cyzest.cyrello.service.UserService;
import io.github.cyzest.commons.spring.web.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api")
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse registerUser(@ModelAttribute @Valid UserRegParam userRegParam) throws Exception {

        UserInfo userInfo = userService.registerUser(userRegParam);

        ApiResponse apiResponse = new ApiResponse(HttpStatus.CREATED);

        apiResponse.putExtra("id", userInfo.getId());

        return apiResponse;
    }

}
