package com.orbcomm.telegram_api_distributor.agent;

import com.orbcomm.telegram_api_distributor.agent.module.ApiGetModuleRunner;
import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import com.orbcomm.telegram_api_distributor.param.CommonParam;
import com.orbcomm.telegram_api_distributor.repository.ApiAccessReceivedViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MessageUpdateAgent {


    Logger logger = LoggerFactory.getLogger(MessageUpdateAgent.class);

    @Autowired
    ApiAccessReceivedViewRepository apiAccessReceivedViewRepository;


    @Autowired
    private ApplicationContext applicationContext;

    @Scheduled(cron = "* * * * * *")
    public void updateMessagesAgent() {

        try {
            List<ApiAccessReceivedView> apiAccessReceivedViews = apiAccessReceivedViewRepository.findByConTypeAndApiQueryTypeAndRequestExpiredLessThanEqual(CommonParam.API_CON_TYPE,CommonParam.API_QUERY_TYPE, LocalDateTime.now());
            for(ApiAccessReceivedView apiAccessReceivedView : apiAccessReceivedViews){
                if(CommonParam.runApiAccessId.get(apiAccessReceivedView.getApiAccessId())==null||
                        CommonParam.runApiAccessId.get(apiAccessReceivedView.getApiAccessId())==false){
                    System.out.println("TEST");

                    ApiGetModuleRunner apiGetModuleRunner = new ApiGetModuleRunner(apiAccessReceivedView,LocalDateTime.now());

                    //의존성 주입
                    applicationContext.getAutowireCapableBeanFactory().autowireBean(apiGetModuleRunner);
                    apiGetModuleRunner.start();

                }


            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
