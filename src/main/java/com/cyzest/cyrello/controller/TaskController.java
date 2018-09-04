package com.cyzest.cyrello.controller;

import com.cyzest.cyrello.dto.ApiResponse;
import com.cyzest.cyrello.dto.TaskInfo;
import com.cyzest.cyrello.dto.TaskRegParam;
import com.cyzest.cyrello.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse registerTask(
            @RequestBody @Valid TaskRegParam taskRegParam, Authentication authentication) throws Exception {

        TaskInfo taskInfo = taskService.registerTask(authentication.getName(), taskRegParam);

        ApiResponse apiResponse = new ApiResponse(HttpStatus.CREATED);

        apiResponse.putExtra("id", taskInfo.getId());

        return apiResponse;
    }

    @PutMapping("/tasks/{id}")
    public ApiResponse updateTask(
            @PathVariable long id,
            @RequestBody @Valid TaskRegParam taskRegParam, Authentication authentication) throws Exception {

        taskService.updateTask(authentication.getName(), id, taskRegParam);

        return new ApiResponse();
    }

    @PostMapping("/tasks/{id}/complete")
    public ApiResponse completeTask(@PathVariable long id, Authentication authentication) throws Exception {

        taskService.completeTask(authentication.getName(), id);

        return new ApiResponse();
    }

}
