package com.botov.sferaHelper.dto;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;

@Data
public class AttributesDto {

    private String attribute;//like "sprint"
    private String identifier;//like "null"
    private String number;//like "FRNRSA-7311"

}
