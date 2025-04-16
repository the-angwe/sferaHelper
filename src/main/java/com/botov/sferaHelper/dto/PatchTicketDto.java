package com.botov.sferaHelper.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PatchTicketDto {
    private Long id;
    private String number;//like FRNRSA-5000
    private Long estimation;//in seconds
    private String parent;//like STROMS-1000
    private Set<String> systems;//like "1553 Заявки ФЛ"
    private Set<String> projectConsumer;//like "f9696ccf-0f8d-431e-a803-9d00ee6e3329" for 2973
    private String dueDate;//like "2024-12-28"
    private String techDebtConsequence; //like "Другое"
    private String workGroup; //like "Технический долг"
    private String archTaskReason; //like "Прочие архитектурные задачи"
}
