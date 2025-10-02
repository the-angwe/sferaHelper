package com.botov.sferaHelper.dto;

import lombok.Data;

@Data public class SprintDto {
    private Long id;
    private String type;
    private String name;
    private String status;
    private String statusCategoryCode;
    private String startDate;
    private String endDate;
}
