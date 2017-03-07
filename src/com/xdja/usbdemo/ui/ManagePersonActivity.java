package com.xdja.usbdemo.ui;


import java.io.File;
import java.util.List;

import com.smartshell.common.ZzLog;
import com.xdja.usbdemo.R;
import com.xdja.usbdemo.bean.PersonBean;
import com.xdja.usbdemo.bean.SaveReturn;
import com.xdja.usbdemo.database.DBHelper;
import com.xdja.usbdemo.http.FingerUtil;
import com.xdja.usbdemo.http.HttpClass;
import com.xdja.usbdemo.http.HttpClass.RequestListener;
import com.xdja.usbdemo.http.HttpRetrofit;
import com.xdja.usbdemo.ui.adpter.PersonListAdpter;
import com.xdja.usbdemo.ui.adpter.PersonListAdpter.OnDeleteClickListener;
import com.xdja.usbdemo.usb.FingerprintUsbDevices;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ManagePersonActivity extends Activity {

    private static final String TAG = "ManagePerson";

    private RelativeLayout titleLayout;
    
    private PersonListAdpter adpter;
    private List<PersonBean> list;

    DBHelper dbHelper;

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_person);

        titleLayout = (RelativeLayout) findViewById(R.id.title);
        TitleBar title = new TitleBar(titleLayout);
        title.hideMenu();
        title.setTitle("Person Information");
        title.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }});
        initPersonList();
        initView();
    }
    private void initPersonList() {
        
        Dlg.show(this, "Get Persons Information");
        dbHelper = new DBHelper(this);
        list = dbHelper.getAllPersons();
        for (PersonBean person: list) {
            ZzLog.i(TAG,  "person = " + person);
        }

        Dlg.dismiss();
    }

    private void initView() {
        ZzLog.i(TAG, "initView");
        listview = (ListView)findViewById(R.id.listview);
        //list = FingerprintUsbDevices.instance(this).getCachedPersons();

        if (list != null) {
            adpter = new PersonListAdpter(this, list);
            adpter.setOnDeleteClickListener(new OnDeleteClickListener (){

                @Override
                public void onClick(final int position) {

                    new AlertDialog.Builder(ManagePersonActivity.this)
                    .setTitle("Caption")
                    .setMessage("Do you want delete is information")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePerson(position);
                        }})
                    .setNegativeButton("Cancel", null)
                    .show();
                }
            });
            listview.setAdapter(adpter);

        } else {
            new AlertDialog.Builder(this)
            .setTitle("Caption:")
            .setMessage("Can not get person information.")
            .setPositiveButton("Confirm", null).show();
        }

    }

    protected void deletePerson(int position) {
        
        PersonBean personBean = list.get(position);
        String id = personBean.getId();

        //deleteFromCloud(id);
        String path = personBean.getImage_path();
        File file = new File(path);
        
        if (file.exists())  file.delete();

        int count = dbHelper.deletePerson(id);
        FingerprintUsbDevices.instance(this).delete(
                Integer.parseInt(personBean.getFingerPirnt_id()));

        if (count > 0) {
            Dlg.alert(this, "Delete", "Remove succeed!");
            list.remove(position);
            adpter.setListData(list);
            adpter.notifyDataSetChanged();
            return;
        }

        Dlg.alert(this, "Delete", "Remove failed!");
    }

    private void deleteFromCloud(String id) {
        // TODO Auto-generated method stub

        Call<SaveReturn> deletePerson = HttpRetrofit.getInstance(this.getApplicationContext())
                .getApiService().deletePerson(id);

        Dlg.show(this, "Delete person information...");

        deletePerson.enqueue(new Callback<SaveReturn>() {
            @Override
            public void onResponse(Response<SaveReturn> response, Retrofit retrofit) {
                if (response != null) {
                    SaveReturn saveReturn = response.body();
                    String message = saveReturn.getRtnMsg();

                    Log.i(TAG, "----onResponse----" + message);
                    
                }
                Log.i(TAG, "----onResponse----");
                Dlg.dismiss();

                adpter.setListData(list);
                adpter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Dlg.dismiss();
                Log.i(TAG, "----onFailure----");
                Toast.makeText(getApplicationContext(), "can not get person"
                        + " information from network.", Toast.LENGTH_LONG).show();
            }
        });
        
    }




}
