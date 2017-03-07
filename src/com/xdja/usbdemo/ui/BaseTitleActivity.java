package com.xdja.usbdemo.ui;


import com.xdja.usbdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class BaseTitleActivity extends Activity {

    protected RelativeLayout titleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView() {
        titleLayout = (RelativeLayout) findViewById(R.id.title);
        TitleBar title = new TitleBar(titleLayout);
        title.hideMenu();
        title.setTitle("Compare Result");
        title.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }});
    }

}
