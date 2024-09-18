package com.botov.sferaHelper.dto;

import lombok.Data;

@Data
public class TicketDto {
    private Long id;
    private String number;//like FRNRSA-5000
    private Long estimation;//in seconds
    private String parent;//like STROMS-1000
}
