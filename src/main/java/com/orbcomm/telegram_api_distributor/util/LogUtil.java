package com.orbcomm.telegram_api_distributor.util;

import com.orbcomm.telegram_api_distributor.entity.UserConnectLog;
import com.orbcomm.telegram_api_distributor.repository.UserConnectLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogUtil {

    Logger logger = LoggerFactory.getLogger(LogUtil.class);

    @Autowired
    UserConnectLogRepository userConnectLogRepository;

    public void setUserLog(String userId,String messageCommand, String conversationId,String message){

        try {

            UserConnectLog userConnectLog = UserConnectLog.builder().userId(userId)
                    .servletPath(messageCommand+(message!=null?"("+message+")":""))
                    .clientIp(conversationId).conType("TELEGRAM").build();
            userConnectLogRepository.save(userConnectLog);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
