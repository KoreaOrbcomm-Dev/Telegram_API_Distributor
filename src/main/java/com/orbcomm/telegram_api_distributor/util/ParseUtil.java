package com.orbcomm.telegram_api_distributor.util;

import com.google.gson.Gson;

import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import com.orbcomm.telegram_api_distributor.param.ApiConnectParam;
import com.orbcomm.telegram_api_distributor.param.CommonParam;
import com.orbcomm.telegram_api_distributor.param.update.TelUpdateResultParam;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ParseUtil {

    @Autowired
    CommonUtil commonUtil;

    DateTimeFormatter saveDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");


    public boolean responseValueChecker(ApiAccessReceivedView apiAccessReceivedView, Map<String, Object> responseBody){

        boolean returnVal = false;

        try {

            Iterator<String> keys = apiAccessReceivedView.getResponseSuccess().keySet().iterator();

            while( keys.hasNext() ) {

                String strKey = keys.next();
                Object strValue = apiAccessReceivedView.getResponseSuccess().get(strKey);
                Object compareValue = responseBody.get(strKey);

                //응답 시 정의된 response_success 및 비교 값이 없을 때 Error
                if(strValue==null || compareValue==null){
                    returnVal = false;
                    break;
                }
                returnVal = commonUtil.dataChecker(strValue,compareValue);


            }


        }catch (Exception e){
            e.printStackTrace();
            commonUtil.apiSendChecker(apiAccessReceivedView.getApiAccessId(),false);
        }


        return returnVal;

    }


    //send_message_history api_access_response_status 데이터
    public Map<String,Object> getApiresponseStatus(ApiAccessReceivedView apiAccessReceivedView, Map<String, Object> responseBody){

        Map<String,Object> returnVal =new HashMap<>();

        try {

            Iterator<String> keys = apiAccessReceivedView.getResponseSuccess().keySet().iterator();

            while( keys.hasNext() ) {
                String strKey = keys.next();
                Object strValue = apiAccessReceivedView.getResponseSuccess().get(strKey);
                Object compareValue = responseBody.get(strKey);

                //System.out.println(strKey);
               // System.out.println(strValue+" : "+strValue.getClass().getSimpleName());
               // System.out.println(compareValue+" : "+compareValue.getClass().getSimpleName());

                if(strValue.getClass().getSimpleName().equals("Integer") && compareValue.getClass().getSimpleName().equals("Double")){
                    compareValue =((Double) compareValue).intValue();
                }else if(strValue.getClass().getSimpleName().equals("Long") && compareValue.getClass().getSimpleName().equals("Double")){
                    compareValue = Long.toString(((Double) compareValue).longValue());
                }else if(strValue.getClass().getSimpleName().equals("String")){
                    if(compareValue==null){
                        compareValue = null;
                    }else if(compareValue.getClass().getSimpleName().equals("Double")) {
                        compareValue = Long.toString(((Double) compareValue).longValue());
                    }
                }
                returnVal.put(strKey,compareValue);
            }

        }catch (Exception e){
            e.printStackTrace();
            returnVal = null;
            //commonUtil.apiSendChecker(apiAccessReceivedView.getApiAccessId(),false);
        }
        return returnVal;

    }

    public Map<String,Object> updateConRequireParam(ApiAccessReceivedView apiAccessReceivedView, List<TelUpdateResultParam> result,long updateId){

        Map<String,Object> returnVal = apiAccessReceivedView.getConRequireParam();
            try {
                //데이터 있을 때 이지만 이전에 updateId를 가져왔으므로
                /*
                Long beforeOffset = Long.getLong(returnVal.get("offset").toString());
                Long offset = beforeOffset;
                if(result.size()>0){
                    for(TelUpdateResultParam resultParam : result){
                        if(offset==null){
                            offset = resultParam.getUpdate_id();
                        }else{
                            if(offset<resultParam.getUpdate_id()){
                                offset = resultParam.getUpdate_id();

                            }
                        }
                    }
                    returnVal.put("offset",++offset);
                } */
                if(updateId!=0){
                    returnVal.put("offset",++updateId);
                }


            }catch (Exception e){
                e.printStackTrace();
                returnVal = null;
            }




        return returnVal;
    }

    public boolean saveDataCheck(ApiConnectParam responseParam){

        boolean returnVal = false;

        File lastDataFolder = new File(CommonParam.DEFAULT_PATH+CommonParam.DATAFILE_PATH+CommonParam.LAST_SAVE_PATH);

        if(!lastDataFolder.exists()){
            lastDataFolder.mkdirs();
        }

        String fileName = responseParam.getApiAccessId()+"&"+responseParam.getApiQueryType()+".txt";
        String filePath = CommonParam.DEFAULT_PATH+CommonParam.DATAFILE_PATH+CommonParam.LAST_SAVE_PATH+fileName;

        File lastDataFile = new File(filePath);

        try {

            if(!lastDataFile.exists()){

                BufferedWriter writer = new BufferedWriter(new FileWriter(lastDataFile));
                writer.write(responseParam.getResponseBody());
                writer.close();

                returnVal = true;
            }else{
                String returnString = null;

                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(lastDataFile));
                String data;
                while ((data = br.readLine())!=null){
                    sb.append(data);
                }
                returnString = sb.toString().replaceAll(System.getProperty("line.separator"), "");
                br.close();
                if(!returnString.equals(responseParam.getResponseBody().replaceAll(System.getProperty("line.separator"), ""))){

                    BufferedWriter writer = new BufferedWriter(new FileWriter(lastDataFile));
                    writer.write(responseParam.getResponseBody());
                    writer.close();
                    returnVal = true;
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }



        return returnVal;
    }

    public String saveResponseData(ApiConnectParam responseParam){

        String returnPath = null;

        String path = responseParam.getApiName();


        //String fileName = responseParam.getApiAccessId()+"_"+responseParam.getApiQueryType()+"_"+saveDateFormat.format(responseParam.getCreateDate())+"_"+Thread.currentThread().getId()+".txt";
        String fileName = responseParam.getApiAccessId()+"&"+responseParam.getApiQueryType()+"&"+saveDateFormat.format(responseParam.getCreateDate())+".txt";
        try {
            File saveFolder = new File(CommonParam.DEFAULT_PATH+CommonParam.DATAFILE_PATH+path);

            if(!saveFolder.exists()){
                saveFolder.mkdirs();
            }

            String saveFilePath = CommonParam.DEFAULT_PATH+CommonParam.DATAFILE_PATH+path+"/"+fileName;

            File dataFile = new File(saveFilePath);

            BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
            writer.write(responseParam.getResponseBody());
            writer.close();

            returnPath = saveFilePath;


        }catch (Exception e){
            e.printStackTrace();
        }

        return returnPath;
    }

    public ApiConnectParam returnCallbackResponse(String apiAccesId, int apiVersion, String saveResponseData
            , Map<String,Object> requestParamMap, String requestBody, HttpServletRequest request){

        ApiConnectParam apiConnectParam = new ApiConnectParam();
        LocalDateTime createDate = LocalDateTime.now();

        try {

            Map<String,Object> requestHeader = returnRequestHeader(request);
            apiConnectParam.setApiAccessId(apiAccesId);
            apiConnectParam.setApiVersion(apiVersion);
            apiConnectParam.setRequestHeader(requestHeader);

            apiConnectParam.setClientIp(request.getRemoteAddr());
            apiConnectParam.setMainUrl(requestHeader.get(CommonParam.CALLBACK_API_HOST).toString());
            apiConnectParam.setSubUrl(request.getRequestURI());
            apiConnectParam.setFullUrl(CommonParam.HTTP_VALUE+apiConnectParam.getMainUrl()+request.getRequestURI());
            apiConnectParam.setRequestParam(requestParamMap);
            apiConnectParam.setCreateDate(createDate);
            apiConnectParam.setResponseBody(requestBody);


            Map<String,Object> requestBodyMap = null;

            try {

                if(requestBody==null || request.equals("")){

                }else{
                    requestBodyMap = new Gson().fromJson(requestBody,Map.class);
                }


            }catch (Exception e){

                requestBodyMap.put("requestBody",requestBody);

            }

            apiConnectParam.setRequestBody(requestBodyMap);


        }catch (Exception e){
            e.printStackTrace();
        }


        return  apiConnectParam;
    }

    public Map<String,Object> returnResponseParserMap(String responseDataType,String responseBody){
        Map<String,Object> responseParserMap = null;

        try {

            switch (responseDataType){
                case "JSON":
                    responseParserMap = new Gson().fromJson(responseBody,Map.class);

                    break;


                default:

                    break;

            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return responseParserMap;
    }


    private Map<String, Object> returnRequestHeader(HttpServletRequest request){

        Map<String, Object> map = null;

        try {

            Enumeration<String> reqestHeader = request.getHeaderNames();
            if(reqestHeader!=null){
                map = new HashMap<>();
                while (reqestHeader.hasMoreElements()){
                    String headerName = reqestHeader.nextElement();
                    map.put(headerName,request.getHeader(headerName));
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return map;
    }

    public boolean callbackChecker(ApiAccessReceivedView apiAccessReceivedView, ApiConnectParam apiConnectParam){

        boolean[] checkArray = new boolean[3];
        checkArray[0] = callbackParamChecker(apiAccessReceivedView.getConRequireParam(),apiAccessReceivedView.getRequestHeader(),apiConnectParam.getRequestHeader(),true);
        checkArray[1] = callbackParamChecker(apiAccessReceivedView.getConRequireParam(),apiAccessReceivedView.getRequestParam(),apiConnectParam.getRequestParam(),false);
        checkArray[2] = callbackParamChecker(apiAccessReceivedView.getConRequireParam(),apiAccessReceivedView.getRequestBody(),apiConnectParam.getRequestBody(),false);

        boolean value = true;
        for(boolean b : checkArray) {
            if(!b) {
                value = false;
                break;
            }
        }

        return value;
    }

    private boolean callbackParamChecker(Map<String,Object> valueMap,Map<String,Object> defaultMap, Map<String,Object> responseMap,boolean header){

        boolean returnVal = true;

        try {

            if(defaultMap==null){


            }else{
                Iterator<String> it = defaultMap.keySet().iterator();

                while (it.hasNext()){

                    String strKey = it.next();
                    Object strValue = defaultMap.get(strKey);

                    if(header){
                        strKey = strKey.toLowerCase();
                    }

                    if(strValue.toString().substring(0,1).equals("@")){

                        String compareKey  = compareKey = strValue.toString().substring(1);

                        if(!valueMap.get(compareKey).toString().equals(responseMap.get(strKey))){
                            returnVal = false;
                            break;
                        }
                    }else{
                       if(!strValue.toString().equals(responseMap.get(strKey).toString())){
                           returnVal = false;
                           break;
                       }
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
            returnVal = false;
        }

        return returnVal;
    }

    public String[] topicReturn(Map<String,String> topicMap){
        try {
            if(topicMap.isEmpty()){
                return null;
            }else{
                Iterator<String> it = topicMap.keySet().iterator();
                String[] returnArray = new String[topicMap.size()];
                int count = 0;
                while (it.hasNext()){
                    String topic = it.next();
                    String wildcard = topicMap.get(topic);
                    //System.out.println(wildcard);
                    String setTopic = null;
                    switch (wildcard.toUpperCase()){
                        case "ALL":
                            setTopic = topic+"#";
                            break;
                        default:
                            setTopic = topic;
                            break;
                    }
                    returnArray[count] = setTopic;
                    count++;
                }
                return returnArray;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public int[] returnQoses(Map<String,String> topicMap){
        try {
            int[] returnQos = new int[topicMap.size()];
            return returnQos;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public ApiAccessReceivedView returnTopicInfo(String getTopic,Map<String,ApiAccessReceivedView> topicMap){

        try {
            ApiAccessReceivedView apiAccessReceivedView = null;
            if(!topicMap.isEmpty()){
                Iterator<String> it = topicMap.keySet().iterator();
                while (it.hasNext()){
                    String topic = it.next();
                    if(getTopic.length()>=topic.length()){
                        String subTopic = getTopic.substring(0,topic.length());
                        if(topic.equals(subTopic)){
                            apiAccessReceivedView = topicMap.get(topic);
                            String remainTopic = "";
                            if(getTopic.length()>topic.length()){
                                remainTopic = getTopic.substring(topic.length());
                                String[] remainTopics = remainTopic.split("/");
                                if(remainTopics.length>2 && (remainTopics[remainTopics.length-1]==null || remainTopics[remainTopics.length-1].equals(""))){
                                    remainTopic = remainTopics[remainTopics.length-2];
                                }else{
                                    remainTopic = remainTopics[remainTopics.length-1];
                                }
                            }

                            apiAccessReceivedView.setApiQueryType(remainTopic);
                            break;
                        }
                    }
                }
            }
            return apiAccessReceivedView;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }




}
