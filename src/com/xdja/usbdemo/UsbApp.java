package com.xdja.usbdemo;

import com.xdja.usbdemo.usb.FingerprintUsbDevices;

import android.app.Application;

public class UsbApp extends Application {
    protected static final String TAG = "UsbApp";
    private static UsbApp instance;

    public static UsbApp getAppContext() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        //FingerprintUsbDevices.instance(getApplicationContext()).powerOn();
    }

}

