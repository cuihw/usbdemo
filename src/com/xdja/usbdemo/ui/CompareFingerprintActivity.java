package com.xdja.usbdemo.ui;

import static com.xdja.usbdemo.ui.CaptureFingerprintActivity.GET_FINGER_PRINT;

import java.util.ArrayList;
import java.util.List;

import com.smartshell.common.ZzLog;
import com.xdja.usbdemo.R;
import com.xdja.usbdemo.bean.PersonBean;
import com.xdja.usbdemo.bean.SaveReturn;
import com.xdja.usbdemo.database.DBHelper;
import com.xdja.usbdemo.http.FingerUtil;
import com.xdja.usbdemo.http.HttpRetrofit;
import com.xdja.usbdemo.usb.FingerprintUsbDevices;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.com.aratek.fp.FingerprintImage;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CompareFingerprintActivity extends Activity {

    private static final String TAG = "CompareActivity";

    RelativeLayout titleLayout;

    LinearLayout menu_layout;

    AnimationDrawable animationDrawable;

    private ImageView compareImg;

    private Button btnCompare;

    FingerprintUsbDevices usbDevice;

    ListView menu_list;
    ArrayAdapter<String> menuAdapter;
    List<String> list = new ArrayList<String>();

    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.arg1) {
            case GET_FINGER_PRINT:
                boolean capture = usbDevice.capture(compareImg);

                if (!capture) {
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

                    showPersonInfo();
                }
                break;
            }

            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_finger);

        titleLayout = (RelativeLayout) findViewById(R.id.title);
        TitleBar title = new TitleBar(titleLayout);
        title.hideBack();
        title.setTitle("Fingerprint Comparison");
        initView();
        initMenu();
        initUsb();
    }

    protected void showPersonInfo() {
        FingerprintImage fp = usbDevice.getFingerprintImage();
        int id = usbDevice.compare(fp);
        if (id < 0) {
            // not found this fingerprint in database.
            Dlg.alert(this, "Compare Result", "Not found this fingerprint. Please try again.");
            btnCompare.setEnabled(true);
            return;
        } else {
            DBHelper dbHelper = new DBHelper(this);
            Dlg.alert(this, "Compare Result", "found fingerprint id = " + id);

            PersonBean person = dbHelper.getPersonByFingerprintId("" + id);
            usbDevice.setMatchedPerson(person);
        }

        Intent intent = new Intent(CompareFingerprintActivity.this,
                CompareResultActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        btnCompare.setEnabled(true);
        super.onResume();
    }

    private void initUsb() {
        usbDevice = FingerprintUsbDevices.instance(this);
        usbDevice.init();
        usbDevice.initScanner();
        usbDevice.powerOn();
    }

    public void initPersonList() {

        Call<SaveReturn> personList = HttpRetrofit
                .getInstance(this)
                .getApiService()
                .getPersonList();

        Dlg.show(this, "get all person information.");
        personList.enqueue(new Callback<SaveReturn>() {
            @Override
            public void onResponse(Response<SaveReturn> response, Retrofit retrofit) {
                if (response != null) {
                    FingerprintUsbDevices.instance(getApplicationContext()).
                          setCachedPersons(FingerUtil.getPersonList(response));
                }
                Log.i(TAG, "----onResponse----");
                Dlg.dismiss();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "----onFailure----");
                Toast.makeText(getApplicationContext(), "can not get person"
                        + " information from network.", Toast.LENGTH_LONG).show();

                Dlg.dismiss();
            }
        });
    }
    
    private void initView() {
        compareImg = (ImageView) findViewById(R.id.compareimg);
        btnCompare = (Button) findViewById(R.id.btn_compare);
    }
    
    


    private void initMenu() {
        menu_layout = (LinearLayout) findViewById(R.id.menu_layout);
        ImageView menu_action = (ImageView) findViewById(R.id.menu_action);

        menu_layout.setVisibility(View.GONE);

        menu_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu_layout.getVisibility() == View.VISIBLE) {
                    hideMenu();
                } else {
                    showMenu();
                }
            }
        });

        list.add("Enroll Person Info");
        list.add("Show All Person Info");

        menu_list = (ListView) findViewById(R.id.menu_list);
    }

    protected void showMenu() {
        menu_layout.setVisibility(View.VISIBLE);
        menuAdapter = new ArrayAdapter<String>(this, R.layout.menu_item, list);
        menu_list.setAdapter(menuAdapter);
        menu_list.setOnItemClickListener(listener);
        menu_list.post(new Runnable() {
            @Override
            public void run() {
                menu_list.requestFocusFromTouch();//获取焦点
            }
        });
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, "position = " + position);

            if (position == 0) {
                startEnrollActivity();
            }else if (position == 1) {
                startManageActivity();
            }
            
            menu_layout.setVisibility(View.GONE);
        }
    };


    protected void hideMenu() {
        menu_layout.setVisibility(View.GONE);
    }


    protected void startManageActivity() {
        Intent intent = new Intent(this, ManagePersonActivity.class);
        startActivity(intent);
    }


    protected void startEnrollActivity() {
        //if (FingerprintUsbDevices.instance(this).powerOn()) {

        usbDevice.openDevice();

        ZzLog.i(TAG, "Start enroll activity");
        Intent intent = new Intent(this, EnrollActivity.class);
        startActivity(intent);
        //}
    }

    public void onClickStart(View view) {
        Log.i(TAG, "onClickStart");
        // begin scan fingerprint;
        usbDevice.openDevice();
        if (!usbDevice.isOpen()) {
            return;
        }

        btnCompare.setEnabled(false);

        animationDrawable = (AnimationDrawable) this.getResources()  
                .getDrawable(R.anim.scan_fingerprint);  
        compareImg.setImageDrawable(animationDrawable);
        animationDrawable.start();

        usbDevice.setMatchedPerson(null);

        Message message = mHandler.obtainMessage();
        message.arg1 = GET_FINGER_PRINT;
        mHandler.sendMessageDelayed(message, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            manager.killBackgroundProcesses(getPackageName());
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
