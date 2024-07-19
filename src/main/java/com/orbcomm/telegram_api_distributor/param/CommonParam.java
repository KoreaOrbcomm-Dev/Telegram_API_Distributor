package com.orbcomm.telegram_api_distributor.param;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

public class CommonParam {

    public static Map<String,Boolean> runApiAccessId = new HashMap<>();
    public static final String API_CON_TYPE = "MESSENGER";
    public static final String API_QUERY_TYPE = "SUBSCRIBE";
    public static final String DATE_VAL =  "DateValue";
    public static final String DEFAULT_PATH = System.getProperty("user.dir")+"/";
    public static final String DATAFILE_PATH = "orbcomm/";
    public static final String LAST_SAVE_PATH = "lastSave/";
    public static final String CALLBACK_CON_TYPE = "CALLBACK";
    public static final String CALLBACK_API_HOST = "host";
    public static final String HTTP_VALUE ="http://";
    public static final String GET_SUBSCRIBE = "SUBSCRIBE";

    public static final String GET_LOGIN = "LOGIN";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM =  "application/x-www-form-urlencoded";



}
