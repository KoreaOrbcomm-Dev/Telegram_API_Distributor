package com.orbcomm.telegram_api_distributor.entity.pk;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiAccessReceivedViewPk implements Serializable {

    private Long apiAccessIndex;
    private String apiQueryType;
}
