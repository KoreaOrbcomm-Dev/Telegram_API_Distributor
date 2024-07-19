package com.orbcomm.telegram_api_distributor.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@ToString
public class ApiConnectParam {//전송 관련 변수

    private String apiAccessId;
    private String apiName;
    private String mainUrl;
    private String subUrl;
    private String fullUrl;
    private LocalDateTime createDate;
    private int connectTimeOut;
    private int readTimeOut;
    private String apiQueryType;
    private String apiRequestType;
    private Map<String, Object> requestHeader;
    private Map<String, Object> requestParam;
    private Map<String, Object> requestBody;
    private int responseCode;
    private String responseBody;
    private Map<String,Object> apiResponseStatus;
    private String lastSavePath;

    private String clientIp;
    private int apiVersion;

    private Map<String,Object> beforeConRequstParam;

}