package com.orbcomm.telegram_api_distributor.util;

import com.orbcomm.telegram_api_distributor.param.CommonParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonUtil {



    public void apiSendChecker(String apiAccessId,boolean type){

        synchronized (CommonParam.runApiAccessId){
            CommonParam.runApiAccessId.put(apiAccessId,type);
        }


    }

    public boolean dataChecker(Object defaultVal, Object compareVal){

        boolean returnVal = false;
        try {

            switch (defaultVal.getClass().getSimpleName()){

                case "Integer":
                    int parseVal =0;
                    switch (compareVal.getClass().getSimpleName()){
                        case "Integer":
                            parseVal = (int) compareVal;
                            break;
                        case "String" :
                            parseVal = Integer.parseInt(compareVal.toString());
                            break;
                        case "Long" :
                            parseVal = ((Long) compareVal).intValue();
                            break;
                        case "Float":
                            parseVal = (int) compareVal;
                            break;
                        case "Double" :
                            parseVal = ((Double) compareVal).intValue();
                            break;
                        default:
                            parseVal = (int) compareVal;
                            break;
                    }

                    if((int)defaultVal==parseVal){
                        returnVal = true;
                    }
                    break;
                case "String" :

                    String parseString = null;
                    switch (compareVal.getClass().getSimpleName()){
                        case "Integer":
                            parseString = Integer.toString((int)compareVal).toUpperCase();
                            break;
                        case "String" :
                            parseString = compareVal.toString().toUpperCase();
                            break;
                        case "Long" :
                            parseString = Long.toString((Long) compareVal).toUpperCase();
                            break;
                        case "Float":
                            parseString = Float.toString((float) compareVal).toUpperCase();
                            break;
                        case "Double" :
                            parseString = Double.toString((Double) compareVal).toUpperCase();
                            break;
                        default:
                            parseString = compareVal.toString().toUpperCase();
                            break;
                    }

                    switch (defaultVal.toString().toUpperCase()){
                        case "NULL":
                            if(parseString==null || parseString.equals("")){
                                returnVal = true;
                            }
                            break;
                        case "NOT NULL":
                            if(parseString!=null && !parseString.equals("")){
                                returnVal = true;
                            }
                            break;
                        default:
                            if(parseString!=null && defaultVal.toString().toUpperCase().equals(parseString)){
                                 returnVal = true;
                            }
                    }

                    break;
                case "Long" :
                    break;
                case "Float":
                    break;
                case "Double" :
                    break;
                default:
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return returnVal;
    }
}
