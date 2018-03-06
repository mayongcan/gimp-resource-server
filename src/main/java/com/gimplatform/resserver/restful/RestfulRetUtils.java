package com.gimplatform.resserver.restful;

import com.alibaba.fastjson.JSONObject;

/**
 * restful接口通用返回码
 * @author zzd
 */
public class RestfulRetUtils {

    private static final String RET_CODE = "RetCode";
    private static final String RET_MSG = "RetMsg";
    private static final String RET_DATA = "RetData";

    private static final String CODE_OK = "000000";

    /**
     * 获取调用成功json
     * @return
     */
    public static JSONObject getRetSuccess(String msg) {
        JSONObject json = new JSONObject();
        json.put(RET_CODE, CODE_OK);
        json.put(RET_DATA, msg);
        return json;
    }

    /**
     * 获取失败返回信息
     * @param code
     * @param msg
     * @return
     */
    public static JSONObject getErrorMsg(String code, String msg) {
        JSONObject json = new JSONObject();
        json.put(RET_CODE, code);
        json.put(RET_MSG, msg);
        return json;
    }
}
