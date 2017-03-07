package com.xdja.usbdemo.ui;


import com.xdja.usbdemo.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TitleBar {
    View view;
    public TitleBar(View view) {
        this.view = view;
    }

    public void hideBack() {
        view.findViewById(R.id.btn_back).setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        TextView titleview = (TextView)view.findViewById(R.id.tvtitle);
        titleview.setText(title);
    }
    public void hideMenu() {
        view.findViewById(R.id.menu_action).setVisibility(View.GONE);
    }
    
    public void setOnBackListener(OnClickListener lisenter) {
        view.findViewById(R.id.btn_back).setOnClickListener(lisenter);
    }
}
