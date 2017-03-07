package com.xdja.usbdemo.ui;

import com.xdja.usbdemo.R;
import com.xdja.usbdemo.usb.FingerprintUsbDevices;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;


public class FlashActivity extends Activity {

    protected static final int START_FINGERPRINT = 1;

    ImageView imageView1;

    public static final String TAG = "FlashActivity";

    AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        imageView1 = (ImageView) findViewById(R.id.compareimg);
        FingerprintUsbDevices.instance(this).init();
        mHandler.sendEmptyMessageDelayed(START_FINGERPRINT, 1500);
    }
    


    @Override
    protected void onResume() {
        animationDrawable = (AnimationDrawable) this.getResources()  
                .getDrawable(R.anim.scan_fingerprint);  
        imageView1.setImageDrawable(animationDrawable);
        animationDrawable.start();
        
        super.onResume();
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_FINGERPRINT:
                    startFingerprintAvtivity();
                    break;
            }
        }
    };

    protected void startFingerprintAvtivity() {
        Intent intent = new Intent(this, CompareFingerprintActivity.class);
        startActivity(intent);
        mHandler.removeCallbacksAndMessages(intent);
        finish();
    }

}
