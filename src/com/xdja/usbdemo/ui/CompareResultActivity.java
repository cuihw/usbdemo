package com.xdja.usbdemo.ui;


import com.xdja.usbdemo.R;
import com.xdja.usbdemo.bean.PersonBean;
import com.xdja.usbdemo.usb.FingerprintUsbDevices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CompareResultActivity extends Activity {

    private static final String TAG = "CompareResult";

    private RelativeLayout titleLayout;

    private ImageView avator;
    private TextView name; 
    private TextView gender; 
    private TextView country;  // 国籍
    private TextView dob; 
    private TextView id_view;
    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_result);

        titleLayout = (RelativeLayout) findViewById(R.id.title);
        TitleBar title = new TitleBar(titleLayout);
        title.hideMenu();
        title.setTitle("Compare Result");
        title.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }});
        initView();

    }

    private void initView() {
        avator = (ImageView) findViewById(R.id.avator);
        name = (TextView) findViewById(R.id.name);
        gender= (TextView) findViewById(R.id.gender);
        country = (TextView) findViewById(R.id.country);
        dob = (TextView) findViewById(R.id.dob);
        id_view = (TextView) findViewById(R.id.id_view);
        address = (TextView) findViewById(R.id.address);

        PersonBean person = FingerprintUsbDevices.instance(this).getMatchedPerson();
        if (person != null) {
            String path = person.getImage_path();
            Bitmap bitmap = BitmapFactory.decodeFile(path);

            avator.setImageBitmap(bitmap);

            name.setText(person.getName() + " " + person.getSurname());
            gender.setText(person.getSex());
            country.setText(person.getCountry());
            dob.setText(person.getDob());
            id_view.setText(person.getIdNo());
            address.setText(person.getAddress());
        }

        if (TextUtils.isEmpty(person.getName()) || "null".equals(person.getName())) {
            new AlertDialog.Builder(this)
            .setTitle("Personal Information:")
            .setMessage("Can not Read Personal Infomation. ")
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }})
            .show();
        }
    }


}
