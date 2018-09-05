package com.cyzest.cyrello.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRegParam {

    @NotEmpty
    @Size(max = 100)
    private String content;

    @Size(max = 10)
    private List<Long> relationTaskIds;

}
