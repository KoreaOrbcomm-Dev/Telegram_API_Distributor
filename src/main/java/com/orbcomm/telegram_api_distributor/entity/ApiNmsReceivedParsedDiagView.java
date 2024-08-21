package com.orbcomm.telegram_api_distributor.entity;

import com.orbcomm.telegram_api_distributor.entity.pk.ApiNmsReceivedParsedDiagViewPk;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@IdClass(ApiNmsReceivedParsedDiagViewPk.class)
@Table(name = "api_nms_received_parsed_diag_view",schema = "gateway")
public class ApiNmsReceivedParsedDiagView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "raw_message_index")
    private Long rawMessageIndex;

    @Column(name = "api_access_id")
    private String apiAccessId;

    @Id
    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "vhcle_nm")
    private String vhcleNm;

    @Column(name = "manage_crp_id")
    private String manageCrpId;

    @Column(name = "manage_crp_nm")
    private String manageCrpNm;

    @Column(name = "crp_id")
    private String crpId;

    @Column(name = "crp_nm")
    private String crpNm;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "event_diff")
    private BigDecimal eventDiff;

    @Column(name = "received_diff")
    private BigDecimal receivedDiff;

    @Column(name = "message_diff")
    private BigDecimal messageDiff;

    @Column(name = "insert_diff")
    private BigDecimal insertDiff;

    @Column(name = "send_diff")
    private BigDecimal sendDiff;

    @Column(name = "group_source")
    private String groupSource;

    @Column(name = "push_type")
    private String pushType;

    @Column(name = "push_address")
    private String pushAddress;

    @Column(name = "push_success")
    private String pushSuccess;

    @Column(name = "parse_message_index")
    private Long parseMessageIndex;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "message_date")
    private LocalDateTime messageDate;

    @Column(name = "insert_date")
    private LocalDateTime insertDate;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "main_key")
    private String mainKey;

    @Column(name = "sub_key")
    private String subKey;

    @Column(name = "warning_min")
    private Integer warningMin;

    @Column(name = "danger_min")
    private Integer dangerMin;

    @Column(name = "min_period")
    private Integer minPeriod;

    @Column(name = "max_period")
    private Integer maxPeriod;

    @Column(name = "day_count")
    private Integer dayCount;

    @Column(name = "message_data")
    private String messageData;

    @Column(name = "message_json",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> messageJson;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "status")
    private String status;

    @Column(name = "diag_date")
    private LocalDateTime diagDate;

    @Column(name = "diag_raw_message_index")
    private Long diagRawMessageIndex;

    @Column(name = "io_json",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> ioJson;

    @Column(name = "st6100_on")
    private Integer st6100On;

    @Column(name = "sat_on_time")
    private Integer satOnTime;

    @Column(name = "sat_cnr")
    private Double satCnr;

    @Column(name = "sat_cut_off_count")
    private Integer satCutOffCount;

    @Column(name = "power_on_count")
    private Integer powerOnCount;

    @Id
    @Column(name = "device_index")
    private Long deviceIndex;

}
