package com.xdja.usbdemo.ori;


import java.text.SimpleDateFormat;
import java.util.Locale;

import com.xdja.usbdemo.R;
import com.xdja.usbdemo.R.drawable;
import com.xdja.usbdemo.R.id;
import com.xdja.usbdemo.R.layout;
import com.xdja.usbdemo.R.string;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.aratek.idcard.IDCard;
import cn.com.aratek.idcard.IDCardReader;
import cn.com.aratek.util.OnUsbPermissionGrantedListener;
import cn.com.aratek.util.Result;


public class ActIDCard extends Activity implements View.OnClickListener {

    private TextView mSN;
    private ImageView mPhoto;
    private EditText mName;
    private EditText mSex;
    private EditText mNationality;
    private EditText mBirthday;
    private EditText mAddress;
    private EditText mNumber;
    private EditText mAuthority;
    private EditText mValidDate;
    private EditText mFinger;
    private Button mBtnOpenOrCloseDevice;
    private Button mBtnReadIdCard;
    private IDCardReader mReader;
    private boolean mDeviceOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReader = new IDCardReader(this);
        mReader.setOnUsbPermissionGrantedListener(new OnUsbPermissionGrantedListener() {
            @Override
            public void onUsbPermissionGranted(boolean isGranted) {
                if (isGranted) {
                    mSN.setText(getString(R.string.idcard_sn, (String) mReader.getSN().data));
                    enableControl(true);
                } else {
                    mSN.setText(getString(R.string.idcard_sn, "null"));
                    enableControl(false);
                }
            }
        });

        setContentView(R.layout.activity_idcard);

        mSN = (TextView) findViewById(R.id.tv_idcard_sn);
        mPhoto = (ImageView) findViewById(R.id.iv_idcard_photo);
        mName = (EditText) findViewById(R.id.et_idcard_name);
        mSex = (EditText) findViewById(R.id.et_idcard_sex);
        mNationality = (EditText) findViewById(R.id.et_idcard_nationality);
        mBirthday = (EditText) findViewById(R.id.et_idcard_birthday);
        mAddress = (EditText) findViewById(R.id.et_idcard_address);
        mNumber = (EditText) findViewById(R.id.et_idcard_number);
        mAuthority = (EditText) findViewById(R.id.et_idcard_authority);
        mValidDate = (EditText) findViewById(R.id.et_idcard_validDate);
        mFinger = (EditText) findViewById(R.id.et_idcard_finger);
        mBtnOpenOrCloseDevice = (Button) findViewById(R.id.bt_open_close);
        mBtnReadIdCard = (Button) findViewById(R.id.bt_readidcard);

        enableControl(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_open_close:
                if (!mDeviceOpened) {
                    openDevice();
                } else {
                    closeDevice();
                }
                break;
            case R.id.bt_readidcard:
                readCard();
                break;
        }
    }

    private void openDevice() {
        int error;
        if ((error = mReader.open()) != IDCardReader.RESULT_OK) {
            Toast.makeText(this, getString(R.string.id_card_reader_open_failed) + error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.id_card_reader_open_success), Toast.LENGTH_SHORT).show();
        }
        mDeviceOpened = true;
        mBtnOpenOrCloseDevice.setText(getString(R.string.close_shenfenzheng));
    }

    private void closeDevice() {
        enableControl(false);
        int error;
        if ((error = mReader.close()) != IDCardReader.RESULT_OK) {
            Toast.makeText(this, getString(R.string.id_card_reader_close_failed) + error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.id_card_reader_close_success), Toast.LENGTH_SHORT).show();
        }
        mDeviceOpened = false;
        mBtnOpenOrCloseDevice.setText(getString(R.string.open_shenfenzheng));
    }

    private void enableControl(boolean enable) {
        if (mReader == null) {
            mBtnOpenOrCloseDevice.setEnabled(false);
            mBtnReadIdCard.setEnabled(false);
        } else {
            mBtnOpenOrCloseDevice.setEnabled(true);
            mBtnReadIdCard.setEnabled(enable);
        }
    }

    private void readCard() {
        mBtnReadIdCard.setEnabled(false);
        Result res = mReader.read();
        if (res.error == IDCardReader.RESULT_OK) {
            Toast.makeText(ActIDCard.this, getString(R.string.id_card_read_success), Toast.LENGTH_SHORT).show();
            IDCard card = (IDCard) res.data;
            showPeopleInfo(card);
        } else if (res.error == IDCardReader.NO_CARD) {
            Toast.makeText(ActIDCard.this, getString(R.string.id_card_not_exist_or_reread), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ActIDCard.this, getString(R.string.id_card_read_failed) + res.error, Toast.LENGTH_SHORT).show();
        }
        mBtnReadIdCard.setEnabled(true);
    }

    private void showPeopleInfo(IDCard card) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        mName.setText(card.getName());
        mSex.setText(card.getSex().toString());
        mNationality.setText(card.getNationality().toString());
        mBirthday.setText(df.format(card.getBirthday()));
        mAddress.setText(card.getAddress());
        mNumber.setText(card.getNumber());
        mAuthority.setText(card.getAuthority());
        mValidDate.setText(df.format(card.getValidFrom()) + " - " + (card.getValidTo() == null ? getString(R.string.long_term) : df.format(card.getValidTo())));
        mFinger.setText(card.isSupportFingerprint() ? R.string.exist : R.string.not_exist);
        if (card.getPhoto() != null) {
            mPhoto.setImageBitmap(card.getPhoto());
        } else {
            mPhoto.setImageResource(R.drawable.nophoto);
        }
    }
}
