package com.orbcomm.telegram_api_distributor.repository;

import com.orbcomm.telegram_api_distributor.entity.ApiAccessInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApiAccessInfoRepository extends JpaRepository<ApiAccessInfo, String>, JpaSpecificationExecutor<ApiAccessInfo> {
    ApiAccessInfo findByApiAccessId(String apiAccessId);
}