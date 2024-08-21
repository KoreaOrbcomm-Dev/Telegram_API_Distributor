package com.orbcomm.telegram_api_distributor.agent.module;


import com.google.gson.Gson;
import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import com.orbcomm.telegram_api_distributor.param.ApiConnectParam;
import com.orbcomm.telegram_api_distributor.param.CommonParam;
import com.orbcomm.telegram_api_distributor.param.update.TelUpdateMessageParam;
import com.orbcomm.telegram_api_distributor.param.update.TelUpdateParam;
import com.orbcomm.telegram_api_distributor.param.update.TelUpdateResultParam;
import com.orbcomm.telegram_api_distributor.service.ApiCollecterService;
import com.orbcomm.telegram_api_distributor.util.CommonUtil;
import com.orbcomm.telegram_api_distributor.util.ParseUtil;
import com.orbcomm.telegram_api_distributor.util.SendUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ApiGetModuleRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApiGetModuleRunner.class);
    private DateTimeFormatter defaultFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private ApiAccessReceivedView apiAccessReceivedView;
    private LocalDateTime createDate;

    @Autowired
    SendUtil sendUtil;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    ParseUtil parseUtil;

    @Autowired
    ApiCollecterService apiCollecterService;

    @Autowired
    ReceivedDataParser receivedDataParser;


    int check = 1;

    public ApiGetModuleRunner(ApiAccessReceivedView apiAccessReceivedView, LocalDateTime createDate){
        this.apiAccessReceivedView = apiAccessReceivedView;
        this.createDate=createDate;
        CommonParam.runApiAccessId.put(this.apiAccessReceivedView.getApiAccessId(),true);
    }

    public void start(){

        logger.info("API [create Thread : {}, ACCESS API Name : {}, CreateDate : {}]"
                ,apiAccessReceivedView.getApiAccessId(),apiAccessReceivedView.getApiName(),defaultFormat.format(createDate));
        run();


    }

    private int run(){

        check = 1;

        new Thread(() -> {

            try{

                if(apiAccessReceivedView.getTokenUseYn().toUpperCase().equals("Y")){
                    if(apiAccessReceivedView.getTokenExpireUse().toUpperCase().equals("Y")){
                        //갱신시간이 현재 시간 보다 빠를 때
                        /*if(createDate.isAfter(apiAccessReceivedView.getTokenExpireDate())){
                            this.apiAccessReceivedView = loginQuery(apiAccessReceivedView);
                        }*/

                    }
                }

                ApiConnectParam requestParam = sendUtil.requestParamSet(apiAccessReceivedView,createDate);
                if(requestParam == null){
                    commonUtil.apiSendChecker(this.apiAccessReceivedView.getApiAccessId(),false);
                }else{
                    //System.out.println(requestParam.toString());
                    ApiConnectParam responseParam = sendUtil.sendGetMessage(requestParam);
                    logger.info("Access ID : {}, Request Param : {}, response Code : {}, response Body: {}",
                            responseParam.getApiAccessId(),responseParam.getRequestParam(),responseParam.getResponseCode(),responseParam.getResponseBody());
                    if(responseParam.getResponseCode()!= HttpURLConnection.HTTP_OK){

                        commonUtil.apiSendChecker(this.apiAccessReceivedView.getApiAccessId(),false);
                        //apiCollecterService.insertSendMessageHistory(responseParam);

                    }else{
                        TelUpdateParam telUpdateParam = new Gson().fromJson(responseParam.getResponseBody(), TelUpdateParam.class);
                        // ApiAccessInfo apiAccessInfo = apiCollecterService.findApiAccessInfo(apiAccessReceivedView.getApiAccessId());
                        long updateId = 0;
                        if(telUpdateParam.getResult().size()>0){
                            List<TelUpdateResultParam> telegramList = new ArrayList<>();
                            for(TelUpdateResultParam resultParam : telUpdateParam.getResult()){
                                String chatId = resultParam.getMessage().getFrom().getId().toString();
                                if(receivedDataParser.denyUser.get(chatId)!=null){
                                    LocalDateTime denyTime = receivedDataParser.denyUser.get(chatId);
                                    if(LocalDateTime.now().isAfter(denyTime)){
                                        telegramList.add(resultParam);

                                        receivedDataParser.denyUser.remove(chatId);
                                    }else {
                                        logger.info("chat ID : "+chatId+", denyTime : "+denyTime +" denied.");
                                    }
                                }else{
                                    telegramList.add(resultParam);
                                }
                                if(resultParam.getUpdate_id()>updateId){
                                    updateId = resultParam.getUpdate_id();
                                }
                            }
                            telUpdateParam.setResult(telegramList);

                        }


                        responseParamSave(apiAccessReceivedView,responseParam,telUpdateParam,updateId);


                    }
                }


            }catch (Exception e){
                e.printStackTrace();
                check = 0;
                commonUtil.apiSendChecker(this.apiAccessReceivedView.getApiAccessId(),false);
            }

        }).start();

        return check;
    }




    private ApiAccessReceivedView responseParamSave(ApiAccessReceivedView apiAccessReceivedView, ApiConnectParam responseParam,TelUpdateParam telUpdateParam,long updateId ){

        Map<String,Object> responseParserMap =null;
        try {

            responseParserMap = parseUtil.returnResponseParserMap(apiAccessReceivedView.getResponseDataType(),responseParam.getResponseBody());


            //XML, JSON에 Header로 붙는 변수가 있을 때
            if(apiAccessReceivedView.getResponseHeaderValue()!=null){
                responseParserMap = (Map<String, Object>) responseParserMap.get(apiAccessReceivedView.getResponseHeaderValue());

            }
            //정상데이터 수신이면(데이터가 있으면)
            if(parseUtil.responseValueChecker(apiAccessReceivedView, responseParserMap)){

                responseParam.setApiResponseStatus(parseUtil.getApiresponseStatus(apiAccessReceivedView,responseParserMap));
                Map<String,Object> conRequireParam = parseUtil.updateConRequireParam(apiAccessReceivedView,telUpdateParam.getResult(),updateId);

                if(conRequireParam==null){

                }else{
                    this.apiAccessReceivedView.setConRequireParam(conRequireParam);
                    apiAccessReceivedView.setConRequireParam(conRequireParam);

                    if(CommonParam.GET_SUBSCRIBE.equals(apiAccessReceivedView.getApiQueryType())){


                        if(telUpdateParam.getResult().size()>0){
                            receivedDataParser.insertReceivedData(telUpdateParam.getResult());
                            apiCollecterService.insertSendMessageHistory(responseParam);
                        }
                        if(updateId>0){
                            apiCollecterService.updateApiAccessInfo(apiAccessReceivedView,responseParam);
                        }


                    }

                }

            }else{//error 혹은 수신데이터 없을 때
                //responseParam.setApiResponseStatus(parseUtil.getApiresponseStatus(apiAccessReceivedView,responseParserMap));
                //apiCollecterService.insertSendMessageHistory(responseParam);
                //apiCollecterService.updateApiAccessInfo(apiAccessReceivedView,responseParam);

            }/**/
            CommonParam.runApiAccessId.put(apiAccessReceivedView.getApiAccessId(),false);

        }catch (Exception e){
            e.printStackTrace();
            commonUtil.apiSendChecker(this.apiAccessReceivedView.getApiAccessId(),false);
        }
        return apiAccessReceivedView;

    }

    /*private ApiAccessReceivedView loginQuery(ApiAccessReceivedView apiAccessReceivedView){

        ApiAccessReceivedView returnReceiveView =null;
        try {

            logger.info(apiAccessReceivedView.getApiAccessId()+"(Login) : "+apiAccessReceivedView.toString());

            ApiAccessReceivedView apiAccessLoginView = apiCollecterService.findByAndApiAccessIdAndApiQueryType(apiAccessReceivedView.getApiAccessId(), CommonParam.GET_LOGIN);


            ApiConnectParam loginReqParam = requestParamSet(apiAccessLoginView,createDate);
            ApiConnectParam loginresParam = sendUtil.sendGetMessage(loginReqParam);
            responseParamSave(apiAccessLoginView,loginresParam);

            returnReceiveView = apiCollecterService.findByAndApiAccessIdAndApiQueryType(apiAccessLoginView.getApiAccessId(), CommonParam.GET_SUBSCRIBE);

        }catch (Exception e){
            e.printStackTrace();
        }

        return returnReceiveView;
    }*/



}
