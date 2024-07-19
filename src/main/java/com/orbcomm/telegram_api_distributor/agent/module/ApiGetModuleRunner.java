package com.orbcomm.telegram_api_distributor.agent.module;


import com.google.gson.Gson;
import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import com.orbcomm.telegram_api_distributor.param.ApiConnectParam;
import com.orbcomm.telegram_api_distributor.param.CommonParam;
import com.orbcomm.telegram_api_distributor.param.update.TelUpdateMessageParam;
import com.orbcomm.telegram_api_distributor.param.update.TelUpdateParam;
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
import java.util.Date;
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




                ApiConnectParam requestParam = requestParamSet(apiAccessReceivedView,createDate);
                //System.out.println(requestParam.toString());
                ApiConnectParam responseParam = sendUtil.sendGetMessage(requestParam);
                logger.info(responseParam.toString());
                if(responseParam.getResponseCode()!= HttpURLConnection.HTTP_OK){




                    commonUtil.apiSendChecker(this.apiAccessReceivedView.getApiAccessId(),false);
                    //apiCollecterService.insertSendMessageHistory(responseParam);

                }else{
                    TelUpdateParam telUpdateParam = new Gson().fromJson(responseParam.getResponseBody(), TelUpdateParam.class);
                    System.out.println(telUpdateParam.toString());
                    System.out.println(requestParam.getResponseBody());
                   // ApiAccessInfo apiAccessInfo = apiCollecterService.findApiAccessInfo(apiAccessReceivedView.getApiAccessId());
                    responseParamSave(apiAccessReceivedView,responseParam,telUpdateParam);

                }/**/



            }catch (Exception e){
                e.printStackTrace();
                check = 0;
                commonUtil.apiSendChecker(this.apiAccessReceivedView.getApiAccessId(),false);
            }

        }).start();

        return check;
    }


    //전송 변수 Making
    private ApiConnectParam requestParamSet(ApiAccessReceivedView apiAccessReceivedView, LocalDateTime date){

        ApiConnectParam requestParam = new ApiConnectParam();

        try {
            String subToken = apiAccessReceivedView.getSubAddress().replaceAll("@authToken",apiAccessReceivedView.getTokenValue());

            requestParam.setApiAccessId(apiAccessReceivedView.getApiAccessId());
            requestParam.setMainUrl(apiAccessReceivedView.getApiMainAddr());
            requestParam.setSubUrl(subToken);
            requestParam.setConnectTimeOut(apiAccessReceivedView.getRequestConnectTimeOut());
            requestParam.setApiRequestType(apiAccessReceivedView.getApiRequestType());
            requestParam.setReadTimeOut(apiAccessReceivedView.getRequestReadTimeOut());
            requestParam.setApiName(apiAccessReceivedView.getApiName());
            requestParam.setApiQueryType(apiAccessReceivedView.getApiQueryType());
            requestParam.setApiRequestType(apiAccessReceivedView.getApiRequestType());
            requestParam.setCreateDate(date);
            requestParam.setBeforeConRequstParam(apiAccessReceivedView.getConRequireParam());

            String sendUrl = apiAccessReceivedView.getApiMainAddr()+subToken;
            requestParam.setFullUrl(sendUrl);


            if(apiAccessReceivedView.getRequestHeader()!=null){

                requestParam.setRequestHeader(sendUtil.requestValueMapper(apiAccessReceivedView.getRequestHeader()
                        ,apiAccessReceivedView.getConRequireParam(),apiAccessReceivedView.getRequestDateFormat(),createDate,apiAccessReceivedView.getRequestLastReceivedSet()));

                logger.info("Access ID : {}, Get RequestHeader : {}"
                        ,apiAccessReceivedView.getApiAccessId(),requestParam.getRequestHeader());
            }
            if(apiAccessReceivedView.getRequestParam()!=null){


                requestParam.setRequestParam(sendUtil.requestValueMapper(apiAccessReceivedView.getRequestParam()
                        ,apiAccessReceivedView.getConRequireParam(),apiAccessReceivedView.getRequestDateFormat(),createDate,apiAccessReceivedView.getRequestLastReceivedSet()));

                logger.info("Access ID : {}, Get RequestParam : {}"
                        ,apiAccessReceivedView.getApiAccessId(),requestParam.getRequestParam());

            }
            if(apiAccessReceivedView.getRequestBody()!=null){

                requestParam.setRequestBody(sendUtil.requestValueMapper(apiAccessReceivedView.getRequestBody()
                        ,apiAccessReceivedView.getConRequireParam(),apiAccessReceivedView.getRequestDateFormat(),createDate,apiAccessReceivedView.getRequestLastReceivedSet()));

                logger.info("Access ID : {}, Get RequestBody : {}"
                        ,apiAccessReceivedView.getApiAccessId(),requestParam.getRequestBody());

            }

            requestParam = sendUtil.sendGetMessage(requestParam);




        }catch (Exception e){
            commonUtil.apiSendChecker(this.apiAccessReceivedView.getApiAccessId(),false);
            e.printStackTrace();
        }

        return requestParam;
    }

    private ApiAccessReceivedView responseParamSave(ApiAccessReceivedView apiAccessReceivedView, ApiConnectParam responseParam,TelUpdateParam telUpdateParam ){

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
                Map<String,Object> conRequireParam = parseUtil.updateConRequireParam(apiAccessReceivedView,telUpdateParam.getResult());

                if(conRequireParam==null){


                }else{
                    apiAccessReceivedView.setConRequireParam(conRequireParam);
                    if(CommonParam.GET_SUBSCRIBE.equals(apiAccessReceivedView.getApiQueryType())){

                        if(parseUtil.saveDataCheck(responseParam)){
                            String lastSavePath = parseUtil.saveResponseData(responseParam);
                            responseParam.setLastSavePath(lastSavePath);
                        }


                    }
                    //apiCollecterService.insertSendMessageHistory(responseParam);
                    apiCollecterService.updateApiAccessInfo(apiAccessReceivedView,responseParam);



                }


            }else{//error 혹은 수신데이터 없을 때
                responseParam.setApiResponseStatus(parseUtil.getApiresponseStatus(apiAccessReceivedView,responseParserMap));
                //apiCollecterService.insertSendMessageHistory(responseParam);
                apiCollecterService.updateApiAccessInfo(apiAccessReceivedView,responseParam);

            }/**/

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
