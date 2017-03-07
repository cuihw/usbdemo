package com.xdja.usbdemo.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdja.usbdemo.bean.PersonBean;
import com.xdja.usbdemo.bean.SaveReturn;

import java.lang.reflect.Type;
import java.util.List;

import retrofit.Response;

/**
 * Created by 蔡小木 on 2016/3/16 0016.
 */
public class FingerUtil {

    private FingerUtil() {}

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 检测wifi是否连接
     *
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 检测3G是否连接
     *
     * @return
     */
    public static boolean is3gConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static PersonBean getPerson(Response<SaveReturn> response) {
        if (response != null) {
            SaveReturn saveReturn = response.body();
            if (saveReturn != null) {
                Object data = saveReturn.getData();
                Gson gson = new Gson();
                String dateJson = gson.toJson(data);
                PersonBean personBean = gson.fromJson(dateJson, PersonBean.class);
//                                        FingerprintUsbDevices.instance(FlashActivity.this).setCachedPerson(personBeen);
                return personBean;
//                if (personBean != null) {
//                    Log.d(TAG, "-------------------personBean-------------------");
//                    Log.d(TAG, personBean.toString());
//
//                }
            }
        }
        return null;
    }

    public static List<PersonBean> getPersonList(Response<SaveReturn> response) {
        if (response != null) {
            SaveReturn saveReturn = response.body();
            if (saveReturn != null) {
                Object data = saveReturn.getData();
                Type type = new TypeToken<List<PersonBean>>() {
                }.getType();
                Gson gson = new Gson();
                String dateJson = gson.toJson(data);
                List<PersonBean> personBeenlist =
                        (List<PersonBean>) gson.fromJson(dateJson, type);

                return personBeenlist;


            }
        }
        return null;
    }
}
