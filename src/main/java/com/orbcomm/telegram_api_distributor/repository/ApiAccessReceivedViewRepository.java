package com.orbcomm.telegram_api_distributor.repository;

import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ApiAccessReceivedViewRepository extends JpaRepository<ApiAccessReceivedView, Void>, JpaSpecificationExecutor<ApiAccessReceivedView> {

}