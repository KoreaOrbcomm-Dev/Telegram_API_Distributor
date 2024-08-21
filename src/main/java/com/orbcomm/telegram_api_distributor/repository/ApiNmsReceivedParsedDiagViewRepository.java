package com.orbcomm.telegram_api_distributor.repository;

import com.orbcomm.telegram_api_distributor.entity.ApiNmsReceivedParsedDiagView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ApiNmsReceivedParsedDiagViewRepository extends JpaRepository<ApiNmsReceivedParsedDiagView, Void>, JpaSpecificationExecutor<ApiNmsReceivedParsedDiagView> {

    List<ApiNmsReceivedParsedDiagView> findByDeviceId(String deviceId);
}