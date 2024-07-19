package com.orbcomm.telegram_api_distributor.repository;

import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


public interface ApiAccessReceivedViewRepository extends JpaRepository<ApiAccessReceivedView, Void>, JpaSpecificationExecutor<ApiAccessReceivedView> {

    List<ApiAccessReceivedView> findByConTypeAndApiQueryTypeAndRequestExpiredLessThanEqual(String conType, String apiQueryType, LocalDateTime localDateTime);
    List<ApiAccessReceivedView> findByRequestExpiredLessThanEqual(LocalDateTime date);
    List<ApiAccessReceivedView> findByApiQueryTypeAndRequestExpiredLessThanEqual(String apiQueryType,LocalDateTime date);
    ApiAccessReceivedView findByAndApiAccessIdAndApiQueryType(String apiAccessId, String apiQueryType);
    ApiAccessReceivedView findByApiAccessIdAndApiVersionAndSubAddressAndConType(String apiAccessId, int apiVersion, String subAddress, String conType);
    List<ApiAccessReceivedView> findByConTypeAndApiQueryType(String contype, String apiQueryType);

}