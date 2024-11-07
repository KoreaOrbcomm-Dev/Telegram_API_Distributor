package com.orbcomm.telegram_api_distributor.repository;

import com.orbcomm.telegram_api_distributor.entity.UserConnectLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserConnectLogRepository extends JpaRepository<UserConnectLog, Long>, JpaSpecificationExecutor<UserConnectLog> {

}