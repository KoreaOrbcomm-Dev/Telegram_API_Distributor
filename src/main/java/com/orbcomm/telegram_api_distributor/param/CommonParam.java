package com.orbcomm.telegram_api_distributor.param;

import com.orbcomm.telegram_api_distributor.param.update.TelUpdateResultParam;
import org.springframework.stereotype.Service;

import java.util.*;

public class CommonParam {

    public static Map<String,Boolean> runApiAccessId = new HashMap<>();
    public static final String API_CON_TYPE = "MESSENGER";
    public static final String API_ACCESS_ID = "KO_NMS_MESSAGE_BOT";
    public static final String API_QUERY_TYPE = "SUBSCRIBE";
    public static final String DATE_VAL =  "DateValue";
    public static final String DEFAULT_PATH = System.getProperty("user.dir")+"/";
    public static final String DATAFILE_PATH = "orbcomm/";
    public static final String LAST_SAVE_PATH = "lastSave/";
    public static final String CALLBACK_CON_TYPE = "CALLBACK";
    public static final String CALLBACK_API_HOST = "host";
    public static final String HTTP_VALUE ="http://";
    public static final String GET_SUBSCRIBE = "SUBSCRIBE";
    public static final String GET_SUBMIT = "SUBMIT";

    public static final String SUB_DETAIL_SEND_MESSAGE = "SEND_MESSAGE";
    public static final String SUB_DETAIL_SEND_LOCATION = "SEND_LOCATION";
    public static final String SUB_DETAIL_SEND_HTML = "SEND_HTML";


    public static final String GET_LOGIN = "LOGIN";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM =  "application/x-www-form-urlencoded";

    public static final int THREAD_QUEUE_COUNT = 10;
    public static final int DENY_MINUTE = 5;

    public static final String HELP_STRING = "/help";
    public static final String CERT_STRING = "/cert";
    public static final String DEVICE_LOCATION_STRING = "/dev_loc";
    public static final String DEVICE_SEARCH_STRING = "/dev_src";
    public static final String DEVICE_NMS_STRING = "/dev_nms";


}
