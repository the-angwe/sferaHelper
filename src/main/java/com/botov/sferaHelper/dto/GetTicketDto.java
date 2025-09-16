package com.botov.sferaHelper.dto;

import com.botov.sferaHelper.bo.TicketType;
import lombok.Data;

import java.util.Set;

import static com.botov.sferaHelper.bo.TicketType.DEFECT;
import static com.botov.sferaHelper.bo.TicketType.TECH_DEBT;

@Data
public class GetTicketDto {
    private Long id;
    private String name;
    private AssigneeDto assignee;
    private String number;//like FRNRSA-5000
    private Long estimation;//in seconds
    private String dueDate;//like "2024-12-28"
    private WorkGroupDto workGroup;
    private TechDebtConsequenceDto techDebtConsequence;
    private TypeDto type;
    private DetectionEnvironmentDto detectionEnvironment;
    private DetectionPhaseDto detectionPhase;

    public boolean isTechDebtIB() {
        if (TicketType.getTicketType(this) == TECH_DEBT
            && techDebtConsequence!=null
            && "ИБ".equals(techDebtConsequence.getName())) {
            return true;
        }
        return false;
    }

    public boolean isProdDefect() {
        if (TicketType.getTicketType(this) == DEFECT
                && ((detectionEnvironment!=null
                && "ПРОМ".equals(detectionEnvironment.getName()))
                || (detectionPhase!=null
                && "ПРОМ".equals(detectionPhase.getName())))) {
            return true;
        }
        return false;
    }
}
