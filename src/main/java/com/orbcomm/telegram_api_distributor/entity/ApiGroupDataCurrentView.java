package com.orbcomm.telegram_api_distributor.entity;

import com.orbcomm.telegram_api_distributor.entity.pk.ApiGroupDataCurrentViewPk;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@IdClass(ApiGroupDataCurrentViewPk.class)
@Table(name = "api_group_data_current_view",schema = "gateway")
public class ApiGroupDataCurrentView implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @Id
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    /*@Column(name = "direction")
    private Integer direction;

    @Column(name = "speed")
    private Integer speed;

    @Column(name = "io_json",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> ioJson;

    @Column(name = "main_key")
    private String mainKey;

    @Column(name = "sub_key")
    private String subKey;*/

}
