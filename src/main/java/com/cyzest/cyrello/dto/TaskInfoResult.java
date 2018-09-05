package com.cyzest.cyrello.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfoResult {

    private int totalCount = 0;
    private List<TaskInfo> taskInfos;

}
