package com.heytz.GWSdkBLEwrapper;


import android.content.Context;
import com.gizwits.gizdataaccess.GizDataAccess;
import com.gizwits.gizdataaccess.GizDataAccessLogin;
import com.gizwits.gizdataaccess.GizDataAccessSource;
import com.gizwits.gizdataaccess.entity.GizDataAccessErrorCode;
import com.gizwits.gizdataaccess.listener.GizDataAccessLoginListener;
import com.gizwits.gizdataaccess.listener.GizDataAccessSourceListener;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chendongdong on 15/11/8.
 */
public class GWSdkBLEwrapper extends CordovaPlugin {

    private CallbackContext cordovaCallbackContext;
    private Context context;
    private String _appId;
    private GizDataAccessLogin gdalogin;
    private GizDataAccessSource gdaSource;

    public GizDataAccessLoginListener gizDataAccessLoginListener = new GizDataAccessLoginListener() {
        public void didLogin(String uid, String token, GizDataAccessErrorCode result, String message) {
            if (result.getResult() == 0 && uid != null && token != null) {
                JSONObject json = new JSONObject();
                try {
                    json.put("uid", uid);
                    json.put("token", token);
                    // 登录成功
                    // ……
                    PluginResult pr = new PluginResult(PluginResult.Status.OK, json);
                    cordovaCallbackContext.sendPluginResult(pr);
                } catch (JSONException e) {
                    //e.printStackTrace();
                    PluginResult pr = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                    cordovaCallbackContext.sendPluginResult(pr);
                }
            } else {
                PluginResult pr = new PluginResult(PluginResult.Status.ERROR, message);
                cordovaCallbackContext.sendPluginResult(pr);
            }
        }
    };

    public GizDataAccessSourceListener gizDataAccessSourceListener = new GizDataAccessSourceListener() {
        public void didSaveData(GizDataAccessSource source, GizDataAccessErrorCode result, String message) {
            if (result.getResult() == 0) {
                // 上传成功
                // ……
                PluginResult pr = new PluginResult(PluginResult.Status.OK, "success");
                cordovaCallbackContext.sendPluginResult(pr);
            }
        }

        public void didLoadData(GizDataAccessSource arg0, JSONArray jsonArray, GizDataAccessErrorCode result, String message) {
            if (result.getResult() == 0) {
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            System.out.println("uid: " + jsonObject.get("uid"));
                            System.out.println("device_sn: " + jsonObject.get("device_sn"));
                            System.out.println("product_key: " + jsonObject.get("product_key"));
                            System.out.println("ts: " + jsonObject.get("ts"));
                            System.out.println("attrs: " + jsonObject.get("attrs"));
                            System.out.println("\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    PluginResult pr = new PluginResult(PluginResult.Status.OK, jsonArray);
                    cordovaCallbackContext.sendPluginResult(pr);
                } else {
                    System.out.println("暂无数据");
                }
            } else {
                System.out.println("读取失败：" + message);
            }
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        context = cordova.getActivity().getApplicationContext();
        gdalogin = new GizDataAccessLogin(gizDataAccessLoginListener);
        gdaSource = new GizDataAccessSource(gizDataAccessSourceListener);
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        cordovaCallbackContext = callbackContext;
        /**
         * 判断是否设置了appId
         */
        if (args.toString(0) != null && !args.toString(0).isEmpty()) {
            if (_appId == null || (_appId.compareToIgnoreCase(args.getString(0)) != 0)) {
                _appId = args.getString(0);
                GizDataAccess.startWithAppId(context, _appId);
            }
        } else {
            PluginResult pr = new PluginResult(PluginResult.Status.ERROR, "appId is null");
            cordovaCallbackContext.sendPluginResult(pr);
            return false;
        }
        /**
         * 登录的方法
         */
        if (action.equals("login")) {
            this.login(args.getString(1), args.getString(2), callbackContext);
            return true;
        }
        if (action.equals("writeData")) {
            List<String> dataList = new ArrayList<String>();
            JSONArray jsonArray = args.getJSONArray(4);

            for (int i = 0; i < jsonArray.length(); i++) {
                dataList.add(jsonArray.get(i).toString());
            }
            this.writeData(args.toString(1), args.toString(2), args.toString(3), dataList, callbackContext);
            return true;
        }
        if (action.equals("readData")) {
            this.readData(args.toString(1), args.toString(2), args.toString(3),
                    Long.parseLong(args.toString(4)),
                    Long.parseLong(args.toString(5)),
                    Integer.parseInt(args.toString(6)),
                    Integer.parseInt(args.toString(7)),
                    callbackContext);
            return true;
        }
        return false;
    }

    /**
     * 如果存在用户名和密码，那么使用这个用户名和密码登录，如果不存在那么使用匿名登录
     *
     * @param username        可选
     * @param password        可选
     * @param callbackContext
     */
    public void login(String username, String password, CallbackContext callbackContext) {

        if (username != null  && password != null &&username != "null"  && password != "null"&& (!username.isEmpty()) && (!password.isEmpty())) {
            gdalogin.login(username, password);
        } else {
            gdalogin.loginAnonymous();
        }
    }

    /**
     * 发送控制命令
     *
     * @param token
     * @param product_key
     * @param device_sn
     * @param data
     * @param callbackContext
     */
    public void writeData(String token, String product_key, String device_sn, List<String> data, CallbackContext callbackContext) {
        gdaSource.saveData(token, product_key, device_sn, data);
    }

    /**
     * @param token
     * @param product_key
     * @param device_sn
     * @param start_time
     * @param end_time
     * @param limit           可选，如果为null 或者 0 默认等于 20
     * @param skip            可选 如果等于 null 默认为 0;
     * @param callbackContext
     */
    public void readData(String token, String product_key, String device_sn, long start_time, long end_time, Integer limit, Integer skip, CallbackContext callbackContext) {
        if (limit == null || limit == 0) {
            limit = 20;
        }
        if (skip == null) {
            skip = 0;
        }
        gdaSource.loadData(token, product_key, device_sn, start_time, end_time, limit, skip);

    }
}
