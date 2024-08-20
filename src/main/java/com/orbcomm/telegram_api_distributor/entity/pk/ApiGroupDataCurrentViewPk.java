package com.orbcomm.telegram_api_distributor.entity.pk;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ApiGroupDataCurrentViewPk implements Serializable {
    private String deviceId;
    private LocalDateTime eventDate;
}
