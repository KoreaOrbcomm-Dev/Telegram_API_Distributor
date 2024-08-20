package com.orbcomm.telegram_api_distributor.repository;

import com.orbcomm.telegram_api_distributor.entity.KakaoBotUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface KakaoBotUserInfoRepository extends JpaRepository<KakaoBotUserInfo, String>, JpaSpecificationExecutor<KakaoBotUserInfo> {

    KakaoBotUserInfo findByMessengerTypeAndCertKeyAndCertYnAndCertExpiredDateGreaterThan(String messengerType, String certKey, String certYn, LocalDateTime certExpiredDate);
}