package com.orbcomm.telegram_api_distributor.entity;

import com.orbcomm.telegram_api_distributor.entity.pk.ApiAccessReceivedViewPk;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Data
@Entity
@IdClass(ApiAccessReceivedViewPk.class)
@Table(name = "api_access_received_view",schema = "gateway")
public class ApiAccessReceivedView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "api_access_index")
    private Long apiAccessIndex;

    @Column(name = "api_access_id")
    private String apiAccessId;

    @Column(name = "api_access_nm")
    private String apiAccessNm;

    @Column(name = "api_name")
    private String apiName;

    @Column(name = "api_main_addr")
    private String apiMainAddr;

    @Column(name = "sub_address")
    private String subAddress;

    @Column(name = "con_type")
    private String conType;


    @Column(name = "con_require_param",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Objects> conRequireParam;

    @Column(name = "token_use_yn")
    private String tokenUseYn;

    @Column(name = "token_value")
    private String tokenValue;

    @Column(name = "token_expire_use")
    private String tokenExpireUse;

    @Column(name = "token_expire_date")
    private LocalDateTime tokenExpireDate;

    @Column(name = "last_received")
    private LocalDateTime lastReceived;

    @Id
    @Column(name = "api_query_type")
    private String apiQueryType;

    @Column(name = "api_request_type")
    private String apiRequestType;

    @Column(name = "request_connect_time_out")
    private Integer requestConnectTimeOut;

    @Column(name = "request_read_time_out")
    private Integer requestReadTimeOut;

    @Column(name = "api_version")
    private Integer apiVersion;

    @Column(name = "api_call_interval")
    private Integer apiCallInterval;

    @Column(name = "request_header",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String,Object> requestHeader;

    @Column(name = "request_param",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String,Object> requestParam;

    @Column(name = "request_body",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String,Object> requestBody;

    @Column(name = "request_date_format")
    private String requestDateFormat;

    @Column(name = "request_last_received_set")
    private Integer requestLastReceivedSet;

    @Column(name = "response_data_type")
    private String responseDataType;

    @Column(name = "response_header_value")
    private String responseHeaderValue;

    @Column(name = "response_param",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String,Object> responseParam;

    @Column(name = "response_success",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String,Object> responseSuccess;

    @Column(name = "response_date_format")
    private String responseDateFormat;

    @Column(name = "request_expired")
    private LocalDateTime requestExpired;

}
