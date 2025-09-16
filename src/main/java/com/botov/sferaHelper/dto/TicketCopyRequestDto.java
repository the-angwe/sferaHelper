package com.botov.sferaHelper.dto;

import lombok.Data;

@Data
public class TicketCopyRequestDto {
    private String entity;//"FRNRSA-9016",
    private OverrideDto override;
    private String[] fields = new String[] {"component","dueDate","sprint","description","links","assignee","label","type","area"};
}
