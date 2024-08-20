package com.orbcomm.telegram_api_distributor.agent.module;

import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import com.orbcomm.telegram_api_distributor.entity.ApiGroupDataCurrentView;
import com.orbcomm.telegram_api_distributor.entity.KakaoBotUserInfo;
import com.orbcomm.telegram_api_distributor.param.ApiConnectParam;
import com.orbcomm.telegram_api_distributor.param.CommonParam;
import com.orbcomm.telegram_api_distributor.param.update.TelUpdateResultParam;
import com.orbcomm.telegram_api_distributor.repository.ApiAccessReceivedViewRepository;
import com.orbcomm.telegram_api_distributor.repository.ApiGroupDataCurrentViewRepository;
import com.orbcomm.telegram_api_distributor.repository.KakaoBotUserInfoRepository;
import com.orbcomm.telegram_api_distributor.util.ParseUtil;
import com.orbcomm.telegram_api_distributor.util.SendUtil;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ReceivedDataParser {

    Logger logger = LoggerFactory.getLogger(ReceivedDataParser.class);

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(CommonParam.THREAD_QUEUE_COUNT);

    @Autowired
    ParseUtil parseUtil;

    @Autowired
    ApiAccessReceivedViewRepository apiAccessReceivedViewRepository;

    @Autowired
    KakaoBotUserInfoRepository kakaoBotUserInfoRepository;


    @Autowired
    ApiGroupDataCurrentViewRepository apiGroupDataCurrentViewRepository;

    @Autowired
    SendUtil sendUtil;

    ApiAccessReceivedView apiAccessSendMessageView =null;
    ApiAccessReceivedView apiAccessSendLocationView =null;

    public Map<String,LocalDateTime> denyUser = new HashMap<>();
    private Map<String,Integer> denyUserCount = new HashMap<>();
    private Map<String,LocalDateTime> lastDenyTime = new HashMap<>();

    public void insertReceivedData(List<TelUpdateResultParam> telUpdateResultParamList){

        try {

            for (TelUpdateResultParam telUpdateResultParam : telUpdateResultParamList) {
                InsertReceivedData insertReceivedData = new InsertReceivedData(telUpdateResultParam);
                insertReceivedData.excutor();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class InsertReceivedData {
        TelUpdateResultParam telUpdateResultParam=null;
        InsertReceivedData(TelUpdateResultParam telUpdateResultParam){
            this.telUpdateResultParam=telUpdateResultParam;
        }

        public void excutor(){
            executor.execute(() -> {
                try {
                    if(apiAccessSendMessageView==null){
                        apiAccessSendMessageView= apiAccessReceivedViewRepository.findByAndApiAccessIdAndApiQueryTypeAndSubDetail(CommonParam.API_ACCESS_ID,CommonParam.GET_SUBMIT,CommonParam.SUB_DETAIL_SEND_MESSAGE);
                    }
                    if(apiAccessSendLocationView==null){
                        apiAccessSendLocationView= apiAccessReceivedViewRepository.findByAndApiAccessIdAndApiQueryTypeAndSubDetail(CommonParam.API_ACCESS_ID,CommonParam.GET_SUBMIT,CommonParam.SUB_DETAIL_SEND_LOCATION);
                    }
                    ApiAccessReceivedView apiAccessSendInfo = apiAccessSendMessageView;

                    String chatId= telUpdateResultParam.getMessage().getFrom().getId().toString();
                    apiAccessSendInfo.getConRequireParam().put("chatId",chatId);
                    ApiConnectParam requestParam = null;
                    boolean failed = true;
                    if(telUpdateResultParam.getMessage().getText()!=null){
                        String[] messageParse = telUpdateResultParam.getMessage().getText().split(" ");
                        KakaoBotUserInfo kakaoBotUserInfo = null;
                       switch (messageParse[0]) {
                           case "/cert":
                               String messageKey = messageParse[0].substring(1);
                               if(messageParse[1]!=null){
                                   String messageValue = messageParse[1];
                                   kakaoBotUserInfo = certParse(messageKey,messageValue,telUpdateResultParam);
                                   if(kakaoBotUserInfo!=null){
                                       apiAccessSendInfo.getConRequireParam().put("sendMessage","[Cert] Success");
                                       failed =false;
                                   }else{
                                       apiAccessSendInfo.getConRequireParam().put("sendMessage","[Cert] Fail");

                                   }
                               }else{
                                   apiAccessSendInfo.getConRequireParam().put("sendMessage","[Cert] Fail");
                               }
                               break;
                           case "/device_location":
                               if(messageParse[1]!=null){

                                   String userId = apiGroupDataCurrentViewRepository.getUserID(chatId);
                                   String deviceId = messageParse[1].trim();
                                   if(userId!=null){
                                       ApiGroupDataCurrentView apiGroupDataCurrentView =
                                               apiGroupDataCurrentViewRepository.getLocationData(userId,deviceId);
                                       if(apiGroupDataCurrentView!=null){
                                           apiAccessSendInfo = apiAccessSendLocationView;
                                           apiAccessSendInfo.getConRequireParam().put("chatId",chatId);
                                           apiAccessSendInfo.getConRequireParam().put("latitude",apiGroupDataCurrentView.getLatitude());
                                           apiAccessSendInfo.getConRequireParam().put("longitude",apiGroupDataCurrentView.getLongitude());
                                           failed =false;
                                       }
                                   }
                               }else{
                                   apiAccessSendInfo.getConRequireParam().put("sendMessage","[Cert] Fail");

                               }

                               break;
                           case "/device_list":
                               String userId = apiGroupDataCurrentViewRepository.getUserID(chatId);
                               if(userId!=null){
                                   ApiGroupDataCurrentView apiGroupDataCurrentView =
                                           apiGroupDataCurrentViewRepository.getLocationData(userId,deviceId);
                                   if(apiGroupDataCurrentView!=null){
                                       apiAccessSendInfo = apiAccessSendLocationView;
                                       apiAccessSendInfo.getConRequireParam().put("chatId",chatId);
                                       apiAccessSendInfo.getConRequireParam().put("latitude",apiGroupDataCurrentView.getLatitude());
                                       apiAccessSendInfo.getConRequireParam().put("longitude",apiGroupDataCurrentView.getLongitude());
                                       failed =false;
                                   }
                               }

                               break;
                           default:

                               apiAccessSendInfo.getConRequireParam().put("sendMessage","Incorrect format");

                               break;
                       }

                   }else{
                        apiAccessSendInfo.getConRequireParam().put("sendMessage","Incorrect text");

                   }

                    if(failed){
                        if(denyUserCount.get(chatId)==null){
                            denyUserCount.put(chatId,1);
                            lastDenyTime.put(chatId,LocalDateTime.now());
                            apiAccessSendInfo.getConRequireParam().put("sendMessage", (apiAccessSendInfo.getConRequireParam().get("sendMessage")+" (Deny Count : "+denyUserCount.get(chatId)+"/5)"));
                        }else if(denyUserCount.get(chatId)<4){
                            if(LocalDateTime.now().isAfter(lastDenyTime.get(chatId).plusSeconds(10))){
                                denyUserCount.put(chatId,1);
                                lastDenyTime.put(chatId,LocalDateTime.now());
                                apiAccessSendInfo.getConRequireParam().put("sendMessage", (apiAccessSendInfo.getConRequireParam().get("sendMessage")+" (Deny Count : "+denyUserCount.get(chatId)+"/5)"));
                            }else{
                                denyUserCount.put(chatId,denyUserCount.get(chatId)+1);
                                apiAccessSendInfo.getConRequireParam().put("sendMessage", (apiAccessSendInfo.getConRequireParam().get("sendMessage")+" (Deny Count : "+denyUserCount.get(chatId)+"/5)"));
                                lastDenyTime.put(chatId,LocalDateTime.now());
                            }

                        }else{
                            denyUserCount.put(chatId,denyUserCount.get(chatId)+1);
                            apiAccessSendInfo.getConRequireParam().put("sendMessage", (apiAccessSendInfo.getConRequireParam().get("sendMessage")+" (This Telegram Account Deny : "+denyUserCount.get(chatId)+"/5)"));
                            denyUser.put(chatId,LocalDateTime.now().plusMinutes(CommonParam.DENY_MINUTE));
                            denyUserCount.remove(chatId);
                        }
                    }
                    requestParam = sendUtil.requestParamSet(apiAccessSendInfo,LocalDateTime.now());
                    if(requestParam!=null){
                        ApiConnectParam responseParam = sendUtil.sendGetMessage(requestParam);
                        if(responseParam.getResponseCode()!= HttpURLConnection.HTTP_OK){
                        }else{

                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
    }

    public KakaoBotUserInfo certParse(String messageKey, String messageValue, TelUpdateResultParam telUpdateResultParam){
        try {


            KakaoBotUserInfo kakaoBotUserInfo = kakaoBotUserInfoRepository.findByMessengerTypeAndCertKeyAndCertYnAndCertExpiredDateGreaterThan("TELEGRAM",messageValue,"Y", LocalDateTime.now());
            if(kakaoBotUserInfo!=null){
                kakaoBotUserInfo.setMsgId(telUpdateResultParam.getMessage().getFrom().getId().toString());
                kakaoBotUserInfo.setUserName(telUpdateResultParam.getMessage().getFrom().getFirst_name());
                kakaoBotUserInfo.setConversationId(telUpdateResultParam.getMessage().getFrom().getId().toString());
                kakaoBotUserInfo.setUseYn("Y");
                kakaoBotUserInfo.setCertYn("N");
                kakaoBotUserInfo.setUpdateDate(LocalDateTime.now());
                kakaoBotUserInfo.setUpdateUser(kakaoBotUserInfo.getUserId());
                kakaoBotUserInfoRepository.save(kakaoBotUserInfo);

                return kakaoBotUserInfo;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // List<User>를 HTML 테이블로 변환
    public String convertToHtmlTable(List<ApiGroupDataCurrentView> apiGroupDataCurrentViewList) {
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<table border='1'>");
        htmlTable.append("<tr><th>Device ID</th><th>Alias</th></tr>");

        for (ApiGroupDataCurrentView apiGroupDataCurrentView : apiGroupDataCurrentViewList) {
            htmlTable.append("<tr>")
                    .append("<td>").append(apiGroupDataCurrentView.getDeviceId()).append("</td>")
                    .append("<td>").append(apiGroupDataCurrentView.getVhcleNm()).append("</td>")
                    .append("</tr>");
        }

        htmlTable.append("</table>");
        return htmlTable.toString();
    }



}
