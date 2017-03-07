package com.xdja.usbdemo.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

/**
 * <b>Description : </b>
 * <p>Created by <a href="mailto:fanjiandong@outlook.com">fanjiandong</a> on 2017/1/16 21:52.</p>
 */

public class UsbPemissionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (FingerprintUsbDevices.ACTION_USB_PERMISSION.equals(action)) {
            synchronized (this) {
                UsbDevice device = (UsbDevice) intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    System.out.println("Authorization");
                    Toast.makeText(context, FingerprintUsbDevices.ACTION_USB_PERMISSION
                            + "Authorization", Toast.LENGTH_LONG).show();

                    FingerprintUsbDevices.instance(context).setScanGranted(true);
                } else {
                    Toast.makeText(context, FingerprintUsbDevices.ACTION_USB_PERMISSION
                            + "UnAuthorization", Toast.LENGTH_LONG).show();

                    FingerprintUsbDevices.instance(context).setScanGranted(false);
                    System.out.println("UnAuthorization");
                    return;
                }
            }
        }
    }
}
