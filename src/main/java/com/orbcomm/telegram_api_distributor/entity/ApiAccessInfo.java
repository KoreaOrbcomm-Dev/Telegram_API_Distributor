package com.orbcomm.telegram_api_distributor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * API 접속 및 계정 정보
 */
@Data
@Entity
@Table(name = "api_access_info",schema = "gateway")
public class ApiAccessInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "api_access_index", nullable = false)
    private Long apiAccessIndex;

    @Column(name = "api_access_id", nullable = false)
    private String apiAccessId;

    @Column(name = "api_access_nm", nullable = false)
    private String apiAccessNm;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    @Column(name = "api_main_addr", nullable = false)
    private String apiMainAddr;

    @Column(name = "con_type", nullable = false)
    private String conType;

    @Column(name = "con_require_param",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> conRequireParam;


    @Column(name = "use_yn", nullable = false)
    private String useYn;

    @Column(name = "token_use_yn", nullable = false)
    private String tokenUseYn;

    @Column(name = "token_value")
    private String tokenValue;

    @Column(name = "token_expire_use")
    private String tokenExpireUse;

    @Column(name = "token_expire_date")
    private LocalDateTime tokenExpireDate;

    @Column(name = "last_received", nullable = false)
    private LocalDateTime lastReceived;

    @Column(name = "last_save_path")
    private String lastSavePath;

    @Column(name = "create_id", nullable = false)
    private String createId;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_id")
    private String updateId;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

}
