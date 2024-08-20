package com.orbcomm.telegram_api_distributor.util;

import com.google.gson.Gson;
import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import com.orbcomm.telegram_api_distributor.param.ApiConnectParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SendUtil {

    private static final Logger logger = LoggerFactory.getLogger(SendUtil.class);

    @Autowired
    CommonUtil commonUtil;


    //전송 변수 비교 및 생성 Util
    public Map<String, Object> requestValueMapper(Map<String, Object> defaultMap, Map<String, Object> convertMap, String dateFormat, LocalDateTime date, int requestLastReceivedSet){
        Map<String,Object> returnMap = new HashMap<>();

        try {

            if(dateFormat!=null&&!dateFormat.equals("")){

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
                List<String> dateValue = (List<String>) convertMap.get("DateValue");

                for(String dateParamValue : dateValue){
                    LocalDateTime setDate = null;
                    try {
                        if(convertMap.get(dateParamValue)==null){
                            setDate = date.plusSeconds(requestLastReceivedSet*(-1));
                        }else{
                            setDate = LocalDateTime.parse(convertMap.get(dateParamValue).toString(),dateTimeFormatter);
                        }


                    }catch (ClassCastException cce){
                        logger.info("con_require_param.DateValue : {} is null(ClassCastException).",dateParamValue);
                        setDate = date;
                    }/*catch (ParseException pe){
                        logger.info("con_require_param.DateValue : {} is null(ParseException).",dateParamValue);

                        setDate = date.plusSeconds(requestLastReceivedSet*(-1));
                    }*/

                    if(convertMap.get(dateParamValue)!=null || setDate!=null){
                        setDate = LocalDateTime.parse(convertMap.get(dateParamValue).toString(),dateTimeFormatter);
                        convertMap.put(dateParamValue,dateTimeFormatter.format(setDate));
                    }
                }
            }

            Iterator<String> keys = defaultMap.keySet().iterator();
            while( keys.hasNext() ){
                String strKey = keys.next();
                Object strValue = defaultMap.get(strKey);

                String getKey = "";
                if(strValue.toString().substring(0,1).equals("@")){
                    System.out.println(strValue.toString());
                    System.out.println(convertMap.toString());
                    getKey = strValue.toString().substring(1);
                    returnMap.put(strKey,convertMap.get(getKey).toString());
                }else{
                    returnMap.put(strKey,strValue);
                }


            }


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return  returnMap;
    }

    public ApiConnectParam sendGetMessage(ApiConnectParam getApiParam){

        try {
            //param을 더해 url 만들기
            URL url = requestParamEncode(getApiParam.getFullUrl(), getApiParam.getRequestParam());
            //System.out.println(url.toString());

            //Connect 생성
            if(url!=null){

                HttpURLConnection conn = createGetApiConn(url,getApiParam);


                if(getApiParam.getRequestBody()!=null&&!getApiParam.getRequestBody().equals("")){
                    byte[] messageBody = requestMessageBody(getApiParam.getRequestBody());

                    try(OutputStream os = conn.getOutputStream()) {
                        os.write(messageBody,0,messageBody.length);
                    }
                }

                StringBuilder sb = new StringBuilder();
                getApiParam.setResponseCode(conn.getResponseCode());

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    getApiParam.setResponseBody(sb.toString());
                    conn.disconnect();
                }else{
                    //연결 오류 발생 시
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            getApiParam = null;
        }

        return getApiParam;
    }


    public URL requestParamEncode(String strUrl, Map<String,Object> map){
        if(map==null){
            return null;
        }

        URL url = null;

        try{
            StringBuilder str = new StringBuilder();
            Set<String> keys = map.keySet();
            boolean first = true;

            str.append(strUrl+"?");
            for (String key:keys){
                Object value = map.get(key);

                if(first){
                    first = false;
                }else{
                    str.append("&");
                }

                try {
                    str.append(URLEncoder.encode(key, "UTF-8")).append("=")
                            .append(URLEncoder.encode(value.toString(), "UTF-8"));
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }

            }

            url = new URL(str.toString());

        }catch (Exception e){

            e.printStackTrace();
            url = null;
        }



        return url;
    }

    private HttpURLConnection createGetApiConn(URL url, ApiConnectParam getApiParam){
        HttpURLConnection conn = null;

        try{
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(getApiParam.getConnectTimeOut()*1000);
            conn.setReadTimeout(getApiParam.getReadTimeOut()*1000);
            conn.setRequestMethod(getApiParam.getApiRequestType());

            if(getApiParam.getRequestHeader()!=null){

                Iterator<String> keys = getApiParam.getRequestHeader().keySet().iterator();

                while( keys.hasNext() ){
                    String strKey = keys.next();
                    String strValue = getApiParam.getRequestHeader().get(strKey).toString();
                    conn.setRequestProperty(strKey,strValue);

                }
            }


        }catch (Exception e){
            e.printStackTrace();
            conn = null;
        }

        return conn;
    }


    private byte[] requestMessageBody(Map<String,Object> map){
        byte[] input = null;

        String messageBody = new Gson().toJson(map).toString();

        input = messageBody.getBytes(StandardCharsets.UTF_8);
        return input;
    }


    public ApiConnectParam requestParamSet(ApiAccessReceivedView apiAccessReceivedView, LocalDateTime date){

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

                requestParam.setRequestHeader(requestValueMapper(apiAccessReceivedView.getRequestHeader()
                        ,apiAccessReceivedView.getConRequireParam(),apiAccessReceivedView.getRequestDateFormat(),date,apiAccessReceivedView.getRequestLastReceivedSet()));

                logger.info("Access ID : {}, Get RequestHeader : {}"
                        ,apiAccessReceivedView.getApiAccessId(),requestParam.getRequestHeader());
            }
            if(apiAccessReceivedView.getRequestParam()!=null){
                requestParam.setRequestParam(requestValueMapper(apiAccessReceivedView.getRequestParam()
                        ,apiAccessReceivedView.getConRequireParam(),apiAccessReceivedView.getRequestDateFormat(),date,apiAccessReceivedView.getRequestLastReceivedSet()));

                logger.info("Access ID : {}, Get RequestParam : {}"
                        ,apiAccessReceivedView.getApiAccessId(),requestParam.getRequestParam());

            }
            if(apiAccessReceivedView.getRequestBody()!=null){

                requestParam.setRequestBody(requestValueMapper(apiAccessReceivedView.getRequestBody()
                        ,apiAccessReceivedView.getConRequireParam(),apiAccessReceivedView.getRequestDateFormat(),date,apiAccessReceivedView.getRequestLastReceivedSet()));

                logger.info("Access ID : {}, Get RequestBody : {}"
                        ,apiAccessReceivedView.getApiAccessId(),requestParam.getRequestBody());

            }

            //requestParam = sendGetMessage(requestParam);
            return requestParam;


        }catch (Exception e){

            e.printStackTrace();
            return null;
        }
    }




}
