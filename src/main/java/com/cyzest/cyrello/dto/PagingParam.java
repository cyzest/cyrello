package com.cyzest.cyrello.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingParam {

    @Min(1)
    private Integer page = 1;

    @Min(1)
    private Integer size = 10;

}
