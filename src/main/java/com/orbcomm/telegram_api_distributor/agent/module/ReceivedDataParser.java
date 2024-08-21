package com.orbcomm.telegram_api_distributor.agent.module;

import com.orbcomm.telegram_api_distributor.entity.ApiAccessReceivedView;
import com.orbcomm.telegram_api_distributor.entity.ApiGroupDataCurrentView;
import com.orbcomm.telegram_api_distributor.entity.ApiNmsReceivedParsedDiagView;
import com.orbcomm.telegram_api_distributor.entity.KakaoBotUserInfo;
import com.orbcomm.telegram_api_distributor.param.ApiConnectParam;
import com.orbcomm.telegram_api_distributor.param.CommonParam;
import com.orbcomm.telegram_api_distributor.param.update.TelUpdateResultParam;
import com.orbcomm.telegram_api_distributor.repository.ApiAccessReceivedViewRepository;
import com.orbcomm.telegram_api_distributor.repository.ApiGroupDataCurrentViewRepository;
import com.orbcomm.telegram_api_distributor.repository.ApiNmsReceivedParsedDiagViewRepository;
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
import java.time.format.DateTimeFormatter;
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
    ApiNmsReceivedParsedDiagViewRepository apiNmsReceivedParsedDiagViewRepository;

    @Autowired
    SendUtil sendUtil;

    ApiAccessReceivedView apiAccessSendMessageView =null;
    ApiAccessReceivedView apiAccessSendLocationView =null;


    String helpString = "/cert : user telegram certification.\n"+
            "/dev_src {Part of a alias or device ID }: Information via device ID or Alias.\n"+
            "/dev_loc {device_id}: device last location.\n"+
            "/dev_nms {device_id}: device NMS Info.";

    String helpStringKor =  "/cert : NMS - Telegram 인증.\n"+
            "/dev_src {text}: Device ID or Alias의 일부(text, 2자 이상)로 Device ID 검색.\n"+
            "/dev_loc {device_id}: Device의 마지막 위치.\n"+
            "/dev_nms {device_id}: 단말의 NMS 정보.";

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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

                    boolean failed = true;
                    List<ApiAccessReceivedView> apiAccessReceivedViewList = new ArrayList<>();
                    if(telUpdateResultParam.getMessage().getText()!=null){
                        String[] messageParse = telUpdateResultParam.getMessage().getText().split(" ");
                        KakaoBotUserInfo kakaoBotUserInfo = null;
                       switch (messageParse[0]) {
                           case CommonParam.HELP_STRING:

                               if(telUpdateResultParam.getMessage().getFrom().getLanguage_code()!=null && telUpdateResultParam.getMessage().getFrom().getLanguage_code().equals("ko")){
                                   apiAccessSendInfo.getConRequireParam().put("sendMessage",helpStringKor);
                               }else{
                                   apiAccessSendInfo.getConRequireParam().put("sendMessage",helpString);
                               }


                               failed =false;
                               break;
                           case CommonParam.CERT_STRING:
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
                           case CommonParam.DEVICE_LOCATION_STRING:
                               if(messageParse[1]!=null){
                                   String userId = apiGroupDataCurrentViewRepository.getUserID(chatId);
                                   String deviceId = messageParse[1].trim();
                                   if(userId!=null){
                                       ApiGroupDataCurrentView apiGroupDataCurrentView =
                                               apiGroupDataCurrentViewRepository.getLocationData(userId,deviceId);
                                       if(apiGroupDataCurrentView!=null){
                                           ApiAccessReceivedView apiAccessSendLocationInfo = apiAccessSendLocationView;
                                           apiAccessSendLocationInfo.getConRequireParam().put("chatId",chatId);
                                           apiAccessSendLocationInfo.getConRequireParam().put("latitude",apiGroupDataCurrentView.getLatitude());
                                           apiAccessSendLocationInfo.getConRequireParam().put("longitude",apiGroupDataCurrentView.getLongitude());
                                           apiAccessReceivedViewList.add(apiAccessSendLocationInfo);

                                           String sendMessage = "Alias : "+apiGroupDataCurrentView.getVhcleNm()+"\n";
                                           sendMessage = sendMessage+"Device ID : "+apiGroupDataCurrentView.getDeviceId()+"\n";
                                           sendMessage = sendMessage+"Location Update : "+dateTimeFormatter.format(apiGroupDataCurrentView.getEventDate())+" (UTC)\n";
                                           sendMessage = sendMessage+"Managed : "+apiGroupDataCurrentView.getManageCrpNm()+"\n";
                                           sendMessage = sendMessage+"Company : "+apiGroupDataCurrentView.getCrpNm()+"\n";
                                           sendMessage = sendMessage+"Lat, Lon : "+apiGroupDataCurrentView.getLatitude()+" , "+apiGroupDataCurrentView.getLongitude();
                                           apiAccessSendInfo.getConRequireParam().put("sendMessage",sendMessage);

                                           failed =false;
                                       }else{
                                           apiAccessSendInfo.getConRequireParam().put("sendMessage","[Device_location] \nThere are no devices available for viewing.");
                                           failed =false;
                                       }
                                   }
                               }else{
                                   apiAccessSendInfo.getConRequireParam().put("sendMessage","[Cert] Fail");
                               }
                               break;
                           case CommonParam.DEVICE_SEARCH_STRING:
                               String userId = apiGroupDataCurrentViewRepository.getUserID(chatId);
                               if(userId!=null && (messageParse[1]!=null && messageParse[1].trim().length()>=2)){
                                   String alias = messageParse[1].trim();
                                   List<ApiGroupDataCurrentView> apiGroupDataCurrentViewList =
                                           apiGroupDataCurrentViewRepository.getDeviceSearch(userId,alias);
                                   if(!apiGroupDataCurrentViewList.isEmpty()){
                                       apiAccessSendInfo.getConRequireParam().put("chatId",chatId);
                                       String sendMessage = convertToTextTable(apiGroupDataCurrentViewList);
                                       if(sendMessage.length()>4096){
                                           apiAccessSendInfo.getConRequireParam().put("sendMessage","[DeviceList] Data Count : "+apiAccessReceivedViewList.size()+" ->> to Many Data.");
                                       }else{
                                           sendMessage = sendMessage+"\n"+"[DeviceList] Data Count : "+apiGroupDataCurrentViewList.size();
                                           apiAccessSendInfo.getConRequireParam().put("sendMessage",sendMessage);
                                       }

                                       failed =false;
                                   }else{
                                       apiAccessSendInfo.getConRequireParam().put("sendMessage","[DeviceList] Empty.");
                                       failed =false;
                                   }
                               }else{
                                   apiAccessSendInfo.getConRequireParam().put("sendMessage","[DeviceList] Search Value too short.");
                               }
                               break;
                           case CommonParam.DEVICE_NMS_STRING:
                               if(messageParse[1]!=null){
                                   String deviceId = messageParse[1].trim();

                                   List<ApiNmsReceivedParsedDiagView> apiNmsReceivedParsedDiagViewList = apiNmsReceivedParsedDiagViewRepository.findByDeviceId(deviceId);
                                   if(apiNmsReceivedParsedDiagViewList!=null && apiNmsReceivedParsedDiagViewList.size()>0){
                                       ApiAccessReceivedView apiAccessSendLocationInfo = apiAccessSendLocationView;
                                       apiAccessSendLocationInfo.getConRequireParam().put("chatId",chatId);
                                       apiAccessSendLocationInfo.getConRequireParam().put("latitude",apiNmsReceivedParsedDiagViewList.get(0).getLatitude());
                                       apiAccessSendLocationInfo.getConRequireParam().put("longitude",apiNmsReceivedParsedDiagViewList.get(0).getLongitude());
                                       apiAccessReceivedViewList.add(apiAccessSendLocationInfo);

                                       String sendMessage = "Alias : "+apiNmsReceivedParsedDiagViewList.get(0).getVhcleNm()+"\n";
                                       sendMessage = sendMessage+"Device ID : "+apiNmsReceivedParsedDiagViewList.get(0).getDeviceId()+"\n";
                                       sendMessage = sendMessage+"Status : "+apiNmsReceivedParsedDiagViewList.get(0).getStatus()+"\n";
                                       sendMessage = sendMessage+"Managed : "+apiNmsReceivedParsedDiagViewList.get(0).getManageCrpNm()+"\n";
                                       sendMessage = sendMessage+"Company : "+apiNmsReceivedParsedDiagViewList.get(0).getCrpNm()+"\n";
                                       if(apiNmsReceivedParsedDiagViewList.get(0).getEventDate()!=null){
                                           sendMessage = sendMessage+"Event Date : "+dateTimeFormatter.format(apiNmsReceivedParsedDiagViewList.get(0).getEventDate())+" (UTC)\n";
                                           sendMessage = sendMessage+"Event Time Gap : "+apiNmsReceivedParsedDiagViewList.get(0).getEventDiff()+"\n";                                       }

                                       sendMessage = sendMessage+"Received Date : "+dateTimeFormatter.format(apiNmsReceivedParsedDiagViewList.get(0).getReceivedDate())+" (UTC)\n";
                                       sendMessage = sendMessage+"Received Time Gap : "+apiNmsReceivedParsedDiagViewList.get(0).getReceivedDiff()+"\n";
                                       sendMessage = sendMessage+"Day Count : "+apiNmsReceivedParsedDiagViewList.get(0).getDayCount()+"\n";

                                       if(apiNmsReceivedParsedDiagViewList.get(0).getMessageJson()!=null && apiNmsReceivedParsedDiagViewList.get(0).getMessageJson().get("RegionName")!=null){
                                           sendMessage = sendMessage+"Region : "+apiNmsReceivedParsedDiagViewList.get(0).getMessageJson().get("RegionName")+"\n";
                                       }

                                       int sendCount = 1;
                                       for(ApiNmsReceivedParsedDiagView apiNmsReceivedParsedDiagView :apiNmsReceivedParsedDiagViewList){
                                           if(apiNmsReceivedParsedDiagView.getGroupSource()!=null){
                                               sendMessage = sendMessage+"\n-------- Send"+(sendCount++) +" --------\n";
                                               sendMessage = sendMessage+"  Sender : "+apiNmsReceivedParsedDiagView.getGroupSource()+"\n";
                                               sendMessage = sendMessage+"  Success : "+apiNmsReceivedParsedDiagView.getPushSuccess()+"\n";
                                               if(apiNmsReceivedParsedDiagView.getPushSuccess().equals("Y")){
                                                   sendMessage = sendMessage+"  Send Date : "+dateTimeFormatter.format(apiNmsReceivedParsedDiagView.getSendDate())+" (UTC)\n";
                                                   sendMessage = sendMessage+"  Send Time Gap : "+apiNmsReceivedParsedDiagView.getSendDiff()+"\n";
                                                   sendMessage = sendMessage+"  Send Type : "+apiNmsReceivedParsedDiagView.getPushType()+"\n";
                                                   sendMessage = sendMessage+"  Address : \n  "+apiNmsReceivedParsedDiagView.getPushAddress()+"\n";
                                               }

                                           }
                                       }
                                       if(sendCount>1){
                                           sendMessage = sendMessage+"-----------------------";
                                       }

                                       if(apiNmsReceivedParsedDiagViewList.get(0).getIoJson()!=null && apiNmsReceivedParsedDiagViewList.get(0).getIoJson().get("period")!=null){
                                           sendMessage = sendMessage+"\n-------- NMS --------\n";
                                           sendMessage = sendMessage+"  NMS Type : "+(apiNmsReceivedParsedDiagViewList.get(0).getIoJson().get("period").toString().equals("1")?"Hourly\n":"Daily\n");
                                           sendMessage = sendMessage+"  Diag Date : "+dateTimeFormatter.format(apiNmsReceivedParsedDiagViewList.get(0).getDiagDate())+" (UTC)\n";
                                           sendMessage = sendMessage+"  Sat Cnr : "+apiNmsReceivedParsedDiagViewList.get(0).getSatCnr()+"\n";
                                           sendMessage = sendMessage+"  Power On Time : "+apiNmsReceivedParsedDiagViewList.get(0).getSt6100On()+"\n";
                                           sendMessage = sendMessage+"  Power On Count : "+apiNmsReceivedParsedDiagViewList.get(0).getPowerOnCount()+"\n";
                                           sendMessage = sendMessage+"  Sat On Time : "+apiNmsReceivedParsedDiagViewList.get(0).getSatOnTime()+"\n";
                                           sendMessage = sendMessage+"  Sat Cut Off : "+apiNmsReceivedParsedDiagViewList.get(0).getSatCutOffCount()+"\n";

                                       }
                                       apiAccessSendInfo.getConRequireParam().put("sendMessage",sendMessage);
                                       failed = false;

                                   }
                               }else{
                                   apiAccessSendInfo.getConRequireParam().put("sendMessage","[NMS] Required Field Empty.");
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
                    if(apiAccessSendInfo!=null){
                        apiAccessReceivedViewList.add(apiAccessSendInfo);
                    }

                    for(ApiAccessReceivedView apiAccessReceivedView: apiAccessReceivedViewList){
                        ApiConnectParam requestParam  = sendUtil.requestParamSet(apiAccessReceivedView,LocalDateTime.now());
                        if(requestParam!=null){
                            ApiConnectParam responseParam = sendUtil.sendGetMessage(requestParam);
                            if(responseParam.getResponseCode()!= HttpURLConnection.HTTP_OK){
                            }else{

                            }
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

    public String convertToTextTable(List<ApiGroupDataCurrentView> apiGroupDataCurrentViewList) {
        StringBuilder textTable = new StringBuilder();

        // 헤더 추가
        textTable.append(String.format("%-10s %-20s\n", "Device ID", "Alias"));
        textTable.append(String.format("%-10s %-20s\n", "----------", "--------------------"));

        // 데이터 추가
        for (ApiGroupDataCurrentView apiGroupDataCurrentView  : apiGroupDataCurrentViewList) {
            textTable.append(String.format("%-10s %-20s\n", apiGroupDataCurrentView.getDeviceId().toString(), apiGroupDataCurrentView.getVhcleNm()));
        }

        return textTable.toString();
    }



}
