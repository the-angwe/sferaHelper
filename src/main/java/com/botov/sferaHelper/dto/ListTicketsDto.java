package com.botov.sferaHelper.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListTicketsDto {

    private Long totalElements;
    private List<TicketDto> content;
}
