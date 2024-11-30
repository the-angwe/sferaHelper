package com.botov.sferaHelper.dto;

import lombok.Data;

import java.util.Set;

@Data
public class GetTicketDto {
    private Long id;
    private String number;//like FRNRSA-5000
    private Long estimation;//in seconds
    private String dueDate;//like "2024-12-28"

}
