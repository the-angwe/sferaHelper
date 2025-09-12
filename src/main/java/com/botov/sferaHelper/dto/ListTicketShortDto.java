package com.botov.sferaHelper.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ListTicketShortDto {
    private Long id;
    private String name;
    private String number;//like FRNRSA-5000
    private Long estimation;//in seconds
    private String parent;//like STROMS-1000
    private Set<SystemDto> systems;//like STROMS-1000
    private String dueDate;//like "2024-12-28"
    private String techDebtConsequence; //like "Другое"
    private String workGroup; //like "Технический долг"
}
