package com.orbcomm.telegram_api_distributor.entity;

import com.orbcomm.telegram_api_distributor.entity.pk.KakaoBotUserInfoPk;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@IdClass(KakaoBotUserInfoPk.class)
@Table(name = "kakao_bot_user_info",schema = "gateway")
public class KakaoBotUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "msg_id")
    private String msgId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "department")
    private String department;

    @Column(name = "dep_position")
    private String depPosition;

    @Column(name = "mobile_num")
    private String mobileNum;

    @Column(name = "conversation_id")
    private String conversationId;

    @Column(name = "use_yn", nullable = false)
    private String useYn;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "update_user")
    private String updateUser;

    @Id
    @Column(name = "messenger_type", nullable = false)
    private String messengerType;

    @Column(name = "cert_yn")
    private String certYn;

    @Column(name = "cert_key")
    private String certKey;

    @Column(name = "cert_expired_date")
    private LocalDateTime certExpiredDate;

}
