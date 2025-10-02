package com.botov.sferaHelper.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListSprintDto {

    private Long totalElements;
    private List<SprintDto> content;
}
