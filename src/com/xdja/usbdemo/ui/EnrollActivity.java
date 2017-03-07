package com.xdja.usbdemo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.smartshell.common.ZzLog;
import com.xdja.usbdemo.R;
import com.xdja.usbdemo.bean.Fingerprint;
import com.xdja.usbdemo.bean.PersonBean;
import com.xdja.usbdemo.bean.SaveReturn;
import com.xdja.usbdemo.database.DBHelper;
import com.xdja.usbdemo.http.HttpRetrofit;
import com.xdja.usbdemo.usb.FingerprintUsbDevices;
import com.xdja.usbdemo.utils.Base64Utils;
import com.xdja.usbdemo.utils.ImgUtil;

import java.io.File;

import cn.com.aratek.fp.FingerprintImage;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class EnrollActivity extends Activity {

    private static final String TAG = "EnrollActivity";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_FINGERPRINT = 2;
    RelativeLayout titleLayout;
    
    private String SdPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/pic";
    private String picTemp = SdPath + "/temp.jpg";
    private boolean hasPhoto;
    private ImageView image_photo;
    private ImageView imageFinger;
    private ImageView delete_fingerprint;
    private ImageView delete_photo;

    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtID;
    //private EditText txtGender;
    private Spinner genderSpinner;
    private String gender = "Male";
    private EditText txtBirthDay;
    private EditText txtCountry;

    private Bitmap photoBitmap;
    private Bitmap fingerBitmap;
    private byte[] fingerFeature;
    private boolean hasFingerprint;

    private String dateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enroll);

        titleLayout = (RelativeLayout) findViewById(R.id.title);
        TitleBar title = new TitleBar(titleLayout);
        title.hideMenu();
        title.setTitle("Personal Information");
        title.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    private void initView() {
        image_photo = (ImageView) findViewById(R.id.image_photo);
        imageFinger = (ImageView) findViewById(R.id.image_fingerprint);
        txtFirstName = (EditText) findViewById(R.id.txt_firstName);
        txtLastName = (EditText) findViewById(R.id.txt_lastName);
        txtID = (EditText) findViewById(R.id.txt_id);
        genderSpinner =  (Spinner) findViewById(R.id.gender);
        txtBirthDay = (EditText) findViewById(R.id.txt_birthday);

        txtBirthDay.setInputType(InputType.TYPE_NULL); 
        txtBirthDay.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService
                            (Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txtBirthDay.getWindowToken(),0);                    
                }
            }
        });

        txtCountry = (EditText) findViewById(R.id.txt_country);
        delete_fingerprint = (ImageView) findViewById(R.id.delete_fingerprint);
        delete_photo = (ImageView) findViewById(R.id.delete_photo);
        delete_fingerprint.setVisibility(View.GONE);
        delete_photo.setVisibility(View.GONE);
        genderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] genders = getResources().getStringArray(R.array.gender);
                gender = genders[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }});

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_birthday:
                dateTimeShow();
                break;
            case R.id.image_photo:
                startCamera();
                break;
            case R.id.image_fingerprint:
                startEnrollFingerprint();
                break;
            case R.id.btn_upload:
                startUploadInfo();
                break;
            case R.id.delete_photo:
                image_photo.setBackgroundResource(R.drawable.photo);
                hasPhoto = false;
                delete_photo.setVisibility(View.GONE);

                File file = new File(picTemp);
                if (file.exists()) file.delete();

                break;

            case R.id.delete_fingerprint:
                imageFinger.setBackgroundResource(R.drawable.photo);
                imageFinger.setImageBitmap(null);
                hasFingerprint = false;
                delete_fingerprint.setVisibility(View.GONE);
                
                int id = FingerprintUsbDevices.instance(this).getFingerprintImageID();
                FingerprintUsbDevices.instance(this).delete(id);

                break;
            default:
                Log.d(TAG, "click default");
        }
    }

    DatePickerDialog picker;

    private void dateTimeShow() {

        picker = new DatePickerDialog(this, null, 1998, 12, 01);
        picker.setCancelable(true);
        picker.setCanceledOnTouchOutside(true);
        picker.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ZzLog.i("Picker", "Correct behavior!");
                dateOfBirth = "" + picker.getDatePicker().getYear();
                int Temp = picker.getDatePicker().getMonth();

                if (Temp < 9) {
                    dateOfBirth = dateOfBirth + "0" + (Temp + 1);
                } else {
                    dateOfBirth = dateOfBirth + (Temp + 1);
                }

                Temp = picker.getDatePicker().getDayOfMonth();

                if (Temp < 9) {
                    dateOfBirth = dateOfBirth + "0" + (Temp);
                } else {
                    dateOfBirth = dateOfBirth + (Temp);
                }

                txtBirthDay.setText(dateOfBirth);
            }
        });
        picker.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Picker", "Cancel!");
                dateOfBirth = null;
                txtBirthDay.setText("");
            }
        });
        picker.show();
    }

    private void startUploadInfo() {
        
        String firstname = txtFirstName.getText().toString();
        String lastName = txtLastName.getText().toString();
        if (!hasFingerprint) {
            Dlg.alert(this, "Caption", "Please capture a fingerprint.");
            return;
        }

        if (!hasPhoto) {
            Dlg.alert(this, "Caption", "Please take a photo.");
            return;
        }
        if (!(TextUtils.isEmpty(firstname) && TextUtils.isEmpty(lastName))) {
            testUpload();
        }
    }

    private void testUpload() {
        final PersonBean personBean = new PersonBean();
        personBean.setCountry(txtCountry.getText().toString());
        personBean.setDob(dateOfBirth);
        personBean.setIdNo(txtID.getText().toString());
        personBean.setName(txtFirstName.getText().toString());
        personBean.setSurname(txtLastName.getText().toString());
        personBean.setSex(gender);
        personBean.setStatus("CITIZEN");
        personBean.setImage_path(picTemp);
        personBean.setAddress("E-commerce building, \nShangDuLu Road NO.166, \nZhengzhou, China");

        if (photoBitmap != null) {
            personBean.setImage(Base64Utils.bitmapToBase64(photoBitmap));
        }

        Fingerprint fingerprint = new Fingerprint();
        if (fingerBitmap != null) {
            fingerprint.setImage(Base64Utils.byteToBase64(fingerFeature));
        }
        FingerprintUsbDevices usbDevices = FingerprintUsbDevices.instance(this);

        int fingerprintImageID = usbDevices.getFingerprintImageID();

        if (fingerprintImageID >= 0) {
            fingerprint.setId(String.valueOf(fingerprintImageID));
            // save devices database.
            personBean.setFingerPirnt_id(String.valueOf(fingerprintImageID));
            Toast.makeText(this, "get fingerprint id = " + fingerprintImageID, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "can not get fingerprint id", Toast.LENGTH_SHORT).show();
        }

        personBean.setFingerprint(fingerprint);

        //uploadToCloud(personBean);
        saveToDatabase(personBean);
    }

    private boolean saveFlag;

    private void saveToDatabase(PersonBean personBean) {
        DBHelper dbHelper = new DBHelper(this);
        int id = dbHelper.addPerson(personBean);
        if (id < 0) {
            Dlg.alert(this, "Caption", "Save Failed.");
            return;
        }
        new AlertDialog.Builder(EnrollActivity.this)
        .setTitle("Save:")
        .setMessage("Save Succeed.")
        .setPositiveButton("Confirm", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveFlag = true;
                
                finish();
            }})
        .show();
    }

    private void uploadToCloud(final PersonBean personBean) {
        Call<SaveReturn> saveReturnCall = HttpRetrofit.getInstance(getApplicationContext())
                .getApiService().savePerson(personBean);
        saveReturnCall.enqueue(new Callback<SaveReturn>() {
            @Override
            public void onResponse(Response<SaveReturn> response, Retrofit retrofit) {
//              Log.d(TAG, "----------------onResponse--------");
                SaveReturn saveReturn = response.body();
                final String rtn = (String)saveReturn.getRtnMsg();
                ZzLog.i(TAG, "response = " + rtn);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        FingerprintUsbDevices.instance(EnrollActivity.this).resetObject();

                        new AlertDialog.Builder(EnrollActivity.this)
                        .setTitle("Caption:")
                        .setMessage("Upload complete " + rtn)
                        .setPositiveButton("Confirm", new OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }})
                        .show();
                        FingerprintUsbDevices.instance(EnrollActivity.this).getCachedPersons().add(personBean);
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(EnrollActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCamera() {
        Long mills = System.currentTimeMillis();


        File dir = new File(SdPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        picTemp = SdPath + "/Temp_" + mills + ".jpg";

        File file = new File(picTemp);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    Bitmap bitmap = ImgUtil.zoomBitmap(picTemp, 500);
                    photoBitmap = bitmap;
                    ImgUtil.saveJPGE_After(bitmap, picTemp, 100);
                    hasPhoto = true;
                    Drawable d = new BitmapDrawable(bitmap);
                    image_photo.setBackgroundDrawable(d);
                    delete_photo.setVisibility(View.VISIBLE);
                    break;
                case REQUEST_FINGERPRINT:
                    byte[] fpBmp = null;
                    Bitmap bitmap1 = null;
                    FingerprintImage fi = FingerprintUsbDevices.instance(this)
                            .getFingerprintImage();
                    if (fi != null && (fpBmp = fi.convert2Bmp()) != null && 
                            (bitmap1 = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) != null) {
                        fingerBitmap = bitmap1;
                        imageFinger.setImageBitmap(bitmap1);
                        delete_fingerprint.setVisibility(View.VISIBLE);

                        fingerFeature = FingerprintUsbDevices.instance(this).getFpFeat();
                        hasFingerprint = true;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    

    private void startEnrollFingerprint() {

        FingerprintUsbDevices usbDevice = FingerprintUsbDevices.instance(this);
        usbDevice.openDevice();
        if (! usbDevice.isOpen()) {
            Dlg.alert(this, "Open device", "Can Not Open Fingerprint Device.");
            return;
        }
        ZzLog.i(TAG, "startEnrollFingerprint");
        Intent intent = new Intent(this, CaptureFingerprintActivity.class);
        startActivityForResult(intent, REQUEST_FINGERPRINT);
    }

    @Override
    protected void onPause() {
        if (hasFingerprint && !saveFlag) {
            FingerprintUsbDevices.instance(this).delete(
                    FingerprintUsbDevices.instance(this).getFingerprintImageID());
        }
        super.onPause();
    }

    
    
}
