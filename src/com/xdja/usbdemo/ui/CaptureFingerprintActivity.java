package com.xdja.usbdemo.ui;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xdja.usbdemo.R;
import com.xdja.usbdemo.usb.FingerprintUsbDevices;

public class CaptureFingerprintActivity extends Activity {

    private static final String TAG = "CaptureFingerprint";

    public static final int GET_FINGER_PRINT = 1;

    RelativeLayout titleLayout;

    FingerprintUsbDevices fingerprintUsbDevices;

    ImageView imageView1;

    AnimationDrawable animationDrawable;

    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.arg1) {
                case GET_FINGER_PRINT:
                    FingerprintUsbDevices device = FingerprintUsbDevices.instance(
                            CaptureFingerprintActivity.this);

                    if (!device.isOpen()) device.openDevice();

                    boolean enroll = device.enroll(imageView1);
                    //boolean enroll = device.capture(imageView1);

                    if (!enroll) {
                        Message message = mHandler.obtainMessage();
                        message.arg1 = GET_FINGER_PRINT;
                        mHandler.sendMessageDelayed(message, 1000);
                    } else {
                        if (animationDrawable != null) {
                            try {
                                animationDrawable.stop();
                            } catch (Exception e) {

                            }
                        }

                        btnOk.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_fingerprint);

        initView();
        titleLayout = (RelativeLayout) findViewById(R.id.title);
        TitleBar title = new TitleBar(titleLayout);
        title.hideMenu();
        title.setTitle("Fingerprint Capture");
        title.setOnBackListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Message message = mHandler.obtainMessage();
        message.arg1 = GET_FINGER_PRINT;
        mHandler.sendMessageDelayed(message, 500);
    }

    @Override
    protected void onPause() {
        FingerprintUsbDevices.instance(CaptureFingerprintActivity.this)
            .setmFingerprintImageView(null);
        super.onPause();
    }



    private void initView() {
        imageView1 = (ImageView) findViewById(R.id.compareimg);
        btnOk = (Button) findViewById(R.id.btn_compare);
        
        animationDrawable = (AnimationDrawable) this.getResources()  
                .getDrawable(R.anim.scan_fingerprint);  
        imageView1.setImageDrawable(animationDrawable);
        animationDrawable.start();
    }

    public void onClickOK(View view) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
