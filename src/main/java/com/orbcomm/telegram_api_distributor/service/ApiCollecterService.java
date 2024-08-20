package com.orbcomm.telegram_api_distributor.service;


import com.orbcomm.telegram_api_distributor.entity.ApiAccessInfo;
import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import com.orbcomm.telegram_api_distributor.entity.SendMessageHistory;
import com.orbcomm.telegram_api_distributor.param.ApiConnectParam;
import com.orbcomm.telegram_api_distributor.param.CommonParam;
import com.orbcomm.telegram_api_distributor.repository.ApiAccessInfoRepository;
import com.orbcomm.telegram_api_distributor.repository.ApiAccessReceivedViewRepository;
import com.orbcomm.telegram_api_distributor.repository.SendMessageHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class ApiCollecterService {

    DateTimeFormatter dateIndexer = DateTimeFormatter.ofPattern("yyyyMMddHH");

    @Autowired
    private ApiAccessReceivedViewRepository apiAccessReceivedViewRepository;

    @Autowired
    private ApiAccessInfoRepository apiAccessInfoRepository;

    @Autowired
    private SendMessageHistoryRepository sendMessageHistoryRepository;



    public List<ApiAccessReceivedView> currentApiViewGetter(LocalDateTime date){
        return apiAccessReceivedViewRepository.findByRequestExpiredLessThanEqual(date);
    }

    public List<ApiAccessReceivedView> currentAPiReceivedViewGetter(String apiQueryType,LocalDateTime date){

        return apiAccessReceivedViewRepository.findByApiQueryTypeAndRequestExpiredLessThanEqual(apiQueryType,date);
    }

    public List<ApiAccessReceivedView> currentMqttSubscribeGetter(String conType,String apiQueryType){
        return apiAccessReceivedViewRepository.findByConTypeAndApiQueryType(conType,apiQueryType);
    }

    public ApiAccessReceivedView findByAndApiAccessIdAndApiQueryType(String apiAccessId,String apiQueryType){
        return apiAccessReceivedViewRepository.findByAndApiAccessIdAndApiQueryType(apiAccessId,apiQueryType);
    }

    public ApiAccessInfo findApiAccessInfo(String  apiAccessId){
        return apiAccessInfoRepository.findByApiAccessId(apiAccessId);
    }

    public void updateApiAccessInfo(ApiAccessReceivedView apiAccessReceivedView, ApiConnectParam responseParam){
        ApiAccessInfo apiAccessInfo = findApiAccessInfo(apiAccessReceivedView.getApiAccessId());
        apiAccessInfo.setConRequireParam(apiAccessReceivedView.getConRequireParam());
        if(responseParam.getLastSavePath()!=null){
            apiAccessInfo.setLastSavePath(responseParam.getLastSavePath());
        }
        apiAccessInfo.setLastReceived(responseParam.getCreateDate());
        apiAccessInfoRepository.save(apiAccessInfo);

    }

    public void insertSendMessageHistory(ApiConnectParam responseParam){

        SendMessageHistory sendMessageHistory = SendMessageHistory.builder().apiAccessId(responseParam.getApiAccessId())
                .apiAccessRequestDate(responseParam.getCreateDate()).apiAccessRequestUrl(responseParam.getFullUrl())
                .apiAccessRequestData(responseParam.getRequestParam()).apiQueryType(responseParam.getApiQueryType())
                .apiRequestType(responseParam.getApiRequestType()).apiAccessResponseStatus(responseParam.getApiResponseStatus())
                .apiAccessStatusCode(responseParam.getResponseCode()).savePath(responseParam.getLastSavePath())
                .dateIndexer(dateIndexer.format(responseParam.getCreateDate())).apiClientIp(responseParam.getClientIp()).build();


        sendMessageHistoryRepository.save(sendMessageHistory);
    }

    public ApiAccessReceivedView findByApiAccessIdAndAndApiVersionAndSubAddress(String apiAccesssId, int apiVerison, String subAddress){
        return  apiAccessReceivedViewRepository.findByApiAccessIdAndApiVersionAndSubAddressAndConType(apiAccesssId,apiVerison,subAddress,CommonParam.CALLBACK_CON_TYPE);
    }

}
