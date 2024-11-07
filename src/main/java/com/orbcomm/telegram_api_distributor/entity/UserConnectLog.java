package com.orbcomm.telegram_api_distributor.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "user_connect_log",schema = "gateway")
public class UserConnectLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_con_index", nullable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userConIndex;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "con_date",nullable = true)
    private LocalDateTime conDate;

    @Column(name = "servlet_path", nullable = false)
    private String servletPath;

    @Column(name = "client_ip", nullable = false)
    private String clientIp;

    @Column(name = "date_indexer",nullable = true)
    private String dateIndexer;

    @Column(name = "con_type")
    private String conType;

    @Builder
    public UserConnectLog(String userId,String servletPath, String clientIp,String conType){
        this.userId = userId;
        this.servletPath = servletPath;
        this.clientIp = clientIp;
        this.conType = conType;
    }


}
