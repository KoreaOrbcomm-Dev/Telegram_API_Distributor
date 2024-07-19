package com.orbcomm.telegram_api_distributor.repository;

import com.orbcomm.telegram_api_distributor.entity.SendMessageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SendMessageHistoryRepository extends JpaRepository<SendMessageHistory, Void>, JpaSpecificationExecutor<SendMessageHistory> {

}