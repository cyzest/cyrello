package com.cyzest.cyrello.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingParam {

    private Integer page = 1;
    private Integer size = 10;

}
