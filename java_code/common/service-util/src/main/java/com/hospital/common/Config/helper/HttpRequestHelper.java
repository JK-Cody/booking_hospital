package com.hospital.common.Config.helper;

import com.alibaba.fastjson.JSONObject;
import com.hospital.common.Config.utils.HttpUtil;
import com.hospital.common.Config.utils.myMD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 请求的对象类型转换工具
 */
@Slf4j
public class HttpRequestHelper {

    public static void main(String[] args) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("d", "4");
        paramMap.put("b", "2");
        paramMap.put("c", "3");
        paramMap.put("a", "1");
        paramMap.put("timestamp", getTimestamp());
        log.info(getSign(paramMap, "111111111"));
    }

    /**
     * Map<String, String[] 转为 Map<String, Object>
     */
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }
        return resultMap;
    }

    /**
     * 解析请求体获取签名
     */
    public static String getSign(Map<String, Object> paramMap, String signKey) {

        if(paramMap.containsKey("sign")) {
            paramMap.remove("sign");
        }
        //解析请求体
        TreeMap<String, Object> sorted = new TreeMap<>(paramMap);
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Object> param : sorted.entrySet()) {
            str.append(param.getValue()).append("|");  //隔开
        }
        //获取签名
        str.append(signKey);
        log.info("HttpRequestHelper加密前：" + str.toString());
//      String md5Str = myMD5.encrypt(str.toString());
        String md5Str = DigestUtils.md5Hex(str.toString());
        log.info("HttpRequestHelper加密后：" + md5Str);
        return md5Str;
    }

    /**
     * 签名校验
     */
    public static boolean isSignEquals(Map<String, Object> paramMap, String signKey) {
        String sign = (String)paramMap.get("sign");
        String md5Str = getSign(paramMap, signKey);
        return sign.equals(md5Str);
    }

    /**
     * 获取时间戳
     */
    public static long getTimestamp() {
        return new Date().getTime();
    }

    /**
     * 封装同步请求
     */
    public static JSONObject sendRequest(Map<String, Object> paramMap, String url){
        String result = "";
        try {
            //封装post参数
            StringBuilder postdata = new StringBuilder();
            for (Map.Entry<String, Object> param : paramMap.entrySet()) {
                postdata.append(param.getKey()).append("=")
                        .append(param.getValue()).append("&");
            }
            log.info(String.format("--> 发送请求：post data %1s", postdata));
            byte[] reqData = postdata.toString().getBytes("utf-8");
            byte[] respdata = HttpUtil.doPost(url,reqData);
            result = new String(respdata);
            log.info(String.format("--> 应答结果：result data %1s", result));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return JSONObject.parseObject(result);
    }
}
