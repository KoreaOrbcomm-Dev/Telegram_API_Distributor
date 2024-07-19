package com.orbcomm.telegram_api_distributor.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "send_message_history",schema = "gateway")
public class SendMessageHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "api_access_log_index",nullable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apiAccessLogIndex;

    @Column(name = "api_access_id", nullable = false)
    private String apiAccessId;

    @Column(name = "api_access_request_date", nullable = false)
    private LocalDateTime apiAccessRequestDate;

    @Column(name = "api_access_request_url", nullable = false)
    private String apiAccessRequestUrl;

    @Column(name = "api_access_request_data",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> apiAccessRequestData;

    @Column(name = "api_query_type", nullable = false)
    private String apiQueryType;

    @Column(name = "api_request_type", nullable = false)
    private String apiRequestType;

    @Column(name = "api_access_response_status",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> apiAccessResponseStatus;

    @Column(name = "api_access_status_code", nullable = false)
    private Integer apiAccessStatusCode;

    @Column(name = "save_path")
    private String savePath;

    @Column(name = "date_indexer", nullable = false)
    private String dateIndexer;

    @Column(name = "api_client_ip")
    private String apiClientIp;

    @Builder
    public SendMessageHistory(String apiAccessId,LocalDateTime apiAccessRequestDate,String apiAccessRequestUrl,
                              Map<String,Object> apiAccessRequestData,String apiQueryType,String apiRequestType,
                              Map<String,Object>  apiAccessResponseStatus,Integer apiAccessStatusCode,
                              String savePath,String dateIndexer,String apiClientIp ) {
        this.apiAccessId = apiAccessId;
        this.apiAccessRequestDate = apiAccessRequestDate;
        this.apiAccessRequestUrl = apiAccessRequestUrl;
        this.apiAccessRequestData = apiAccessRequestData;
        this.apiQueryType = apiQueryType;
        this.apiRequestType = apiRequestType;
        this.apiAccessResponseStatus = apiAccessResponseStatus;
        this.apiAccessStatusCode = apiAccessStatusCode;
        this.savePath = savePath;
        this.dateIndexer = dateIndexer;
        this.apiClientIp = apiClientIp;

    }

}
