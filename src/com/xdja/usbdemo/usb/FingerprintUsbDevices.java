package com.xdja.usbdemo.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.smartshell.common.ZzLog;
import com.viewtool.USBDriver.ErrorType;
import com.viewtool.USBDriver.UsbDriver;
import com.xdja.usbdemo.R;
import com.xdja.usbdemo.bean.Fingerprint;
import com.xdja.usbdemo.bean.PersonBean;
import com.xdja.usbdemo.ori.Config;
import com.xdja.usbdemo.ori.MainActivity;
import com.xdja.usbdemo.utils.SharePrefUtil;

import java.util.List;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.OnUsbPermissionGrantedListener;
import cn.com.aratek.util.Result;

public class FingerprintUsbDevices {

    // 权限
    public static final String ACTION_USB_PERMISSION = "com.smartshell.usbdemo.USB_PERMISSION";

    private static final String TAG = "FingerprintUsbDevices";

    private static final String FP_DB_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/xdja/fp.db";

    // 接口类
    UsbDriver mUsbDriver;

    UsbManager mUsbManager;

    UsbDevice mUsbDevice;

    MyHandler mHandler;

    FingerprintScanner mScanner;

    Context context;

    UsbReceiver usbReceiver;

    UsbPemissionReceiver usbPemissionReceiver;

    private List<PersonBean> cachedPerson;
    
    private PersonBean matchedPerson;

    private byte[] fpFeat;
    
    private boolean isUsbGranted;

    private boolean isScanGranted2;

    private FingerprintImage fingerprintImage;

    private int fingerprintImageID;

    public void setScanGranted (boolean granted) {
        isUsbGranted = granted;
    }

    public void setCachedPersons(List<PersonBean> cachedPerson) {
        this.cachedPerson = cachedPerson;
    }

    public PersonBean getMatchedPerson() {
        return matchedPerson;
    }

    public void setMatchedPerson(PersonBean person){
        matchedPerson = person;
    }
    
    public List<PersonBean> getCachedPersons() {
        return cachedPerson;
    }

    private boolean mDeviceOpened = false;

    private boolean mDevicePowerOn = false;

    public boolean isOpen() {
        return mDeviceOpened;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0x21) {
                reset_usb();
            } else if (msg.arg1 == 0x22) {
                release_usb();
            }
        }
    }

    /**
     * 配置usb
     */
    private boolean reset_usb() {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        // set us
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, new Intent(ACTION_USB_PERMISSION), 0);

        mUsbDriver = new UsbDriver(mUsbManager, pendingIntent);
        mUsbDevice = mUsbDriver.ScanDevices();

        if (mUsbDevice != null) {
            toastMessage("find usb device");
            return true;
        } else {
            toastMessage("not found usb device");
            return false;
        }
    }


    public int getFingerprintImageID() {
        return fingerprintImageID;
    }

    public void resetObject(){
        fingerprintImage = null;
        fingerprintImageID = -1;
        fpFeat = null;
    }

    private static FingerprintUsbDevices instance;

    public static FingerprintUsbDevices instance(Context context) {
        if (instance == null) {
            synchronized (FingerprintUsbDevices.class) {
                if (instance == null) {
                    instance = new FingerprintUsbDevices(
                            context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void init() {
        mHandler = new MyHandler();
        usbReceiver = new UsbReceiver(this.context, mHandler);
        usbReceiver.registerReceiver();

        usbPemissionReceiver = new UsbPemissionReceiver();
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(usbPemissionReceiver, filter);
    }

    public FingerprintImage getFingerprintImage() {
        return fingerprintImage;
    }

    private FingerprintUsbDevices(Context context) {
        this.context = context.getApplicationContext();


    }

    public void initScanner() {

        mScanner = new FingerprintScanner(context);

        mScanner.setOnUsbPermissionGrantedListener(new OnUsbPermissionGrantedListener() {

            @Override
            public void onUsbPermissionGranted(boolean isGranted) {
                // isScanGranted = isGranted;

                isScanGranted2 = isGranted;
                toastMessage("onUsbPermissionGranted isGranted = " + isGranted);
                if (isGranted) {
                    ZzLog.i(TAG, context.getString(R.string.fps_sn, (String) mScanner.getSN().data));
                    ZzLog.i(TAG, context.getString(R.string.fps_fw, (String) mScanner.getFirmwareVersion().data));
                } else {
                    ZzLog.i(TAG, context.getString(R.string.fps_sn, "null"));
                    ZzLog.i(TAG, context.getString(R.string.fps_fw, "null"));
                }
            }
        });
    }
    
    

    public boolean config_usb() {

        if (null == mUsbDriver || mUsbDevice == null) {
            mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
            mUsbDriver = new UsbDriver(mUsbManager, pendingIntent);
            mUsbDevice = mUsbDriver.ScanDevices();
        }

        if (mUsbDevice != null) {
            ZzLog.i(TAG, "find usb device");
            return true;
        } else {
            ZzLog.i(TAG, "not found usb device");
            return false;
        }
    }

    /**
     * release
     */
    private void release_usb() {
        mUsbDriver = null;
        mUsbDevice = null;
    }

    // power on
    public boolean powerOn() {
        if (mDevicePowerOn) {
            return mDevicePowerOn;
        }

        if (false == config_usb()) {
            ZzLog.i(TAG, "open fingerprint device failed. device is not connection.");
            toastMessage("not found UsbReceiver device");
            return false;
        }

        byte[] cmd = new byte[]{0x0A, 0x15, 0x01, 0x00, 0x20, 0x0B};
        byte[] ret = sendCmd(cmd, "", null); // 供电
        if (ret != null && ret[0] == ((byte) 0x0B) && ret[1] == ((byte) 0x00) && ret[2] == ((byte) 0x00)
                && ret[3] == ((byte) 0x0B) && ret[4] == ((byte) 0x0A)) {

            toastMessage("powerOn device success.");
            mDevicePowerOn = true;
            return true;
        } else {
            toastMessage("power on device failed.");

            mDevicePowerOn = false;
            return false;
        }
    }



    // power off
    public boolean powerOff() {
        /**
         * 关闭指纹模块
         */

        byte[] cmd = new byte[]{0x0A, 0x15, 0x01, 0x01, 0x21, 0x0B};
        byte[] ret = sendCmd(cmd, "", null);
        if (ret != null && ret[0] == ((byte) 0x0B) && ret[1] == ((byte) 0x00)
                && ret[2] == ((byte) 0x00)
                && ret[3] == ((byte) 0x0B) && ret[4] == ((byte) 0x0A)) {
            ZzLog.i(TAG, "close fingerprint device success.");
            mDevicePowerOn = false;
            return true;
        } else {
            ZzLog.i(TAG, "close fingerprint device failed.");
            mDevicePowerOn = false;
            return false;
        }
        
        
    }

    private byte[] sendCmd(byte[] cmd, String startDesc, String sendSuccessDesc) {
        int ret;
        if (mUsbDevice == null) {
            return null;
        }
        ret = mUsbDriver.OpenDevice();
        if (ret != ErrorType.ERR_SUCCESS) {
            return null;
        } else {
            // Open Device success;
            byte[] WriteData = cmd;
            if (!TextUtils.isEmpty(startDesc)) {
                appendConsole("-- "+startDesc+" --");
            }

            int i_USBWriteData_ep1 = mUsbDriver.USBWriteData(Config.EP1_OUT, WriteData, WriteData.length, 1000);
            if (i_USBWriteData_ep1 != WriteData.length) {
                appendConsole("USBWriteData error.");
            } else {
                if (!TextUtils.isEmpty(sendSuccessDesc)) {
                    appendConsole("-- "+sendSuccessDesc+" --");
                }
            }

            byte[] readData = new byte[64];
            // 读取返回
            int i_USBReadData_ep1 = mUsbDriver.USBReadData(Config.EP1_IN, readData, 64, 1000);
            return readData;
        }
    }

    private void appendConsole(String string) {
        ZzLog.i(TAG, string);
    }

    public int compare(FingerprintImage fingerprint) {
        int id = -1;
        Result res = Bione.extractFeature(fingerprint);

        if (res.error == Bione.RESULT_OK) {
            byte[] fpFeat = (byte[]) res.data;
            id = compare(fpFeat);
        }
        return id;
    }

    
    public int compare(byte[] fpFeature) {
        int id = Bione.identify(fpFeature);
        return id;
    }

    public int enroll_fingerprint(byte[] fpFeat) {
        int id = -1;
        Result res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat);
        if (res.error == Bione.RESULT_OK) {
            byte[] fpTemp = (byte[]) res.data;
            int retid = Bione.getFreeID();
            if (retid < 0) {
                ZzLog.i(TAG, "can't get a free id.");
            } else {
                int ret = Bione.enroll(id, fpTemp);
                if (ret == Bione.RESULT_OK) {
                    id = retid;
                }
            }
        }
        fingerprintImageID = id;
        return id;
    }
    

    public boolean enroll_fingerprint(int id, byte[] fpFeat) {

        Result res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat);
        if (res.error == Bione.RESULT_OK) {
            byte[] fpTemp = (byte[]) res.data;
            //int retid = Bione.getFreeID();
            int ret = Bione.enroll(id, fpTemp);
            if (ret == Bione.RESULT_OK) {
                return true;
            } else {
                ZzLog.i(TAG, "enroll ok");
            }
        }
        return false;
    }

    public int enroll_fingerprint(FingerprintImage fingerprint) {
        int id = -1;

        Result res = Bione.extractFeature(fingerprint);
        if (res.error == Bione.RESULT_OK) {
            byte[] fpFeat = (byte[]) res.data;
            id = enroll_fingerprint(fpFeat);
        }
        return id;
    }

    ImageView mFingerprintImageView;
    
    public ImageView getmFingerprintImageView() {
        return mFingerprintImageView;
    }

    public void setmFingerprintImageView(ImageView mFingerprintImageView) {
        this.mFingerprintImageView = mFingerprintImageView;
    }

    /**
     * 指纹录入
     */
    public boolean enroll(ImageView view) {
        
        mFingerprintImageView = view;
        
        if (!isScanGranted2) {
            initScanner();
            return false;
        }
        mScanner.prepare();
        long startTime = System.currentTimeMillis();
        Result res = mScanner.capture();
        long captureTime = System.currentTimeMillis() - startTime;
        mScanner.finish();
        if (res.error != FingerprintScanner.RESULT_OK) {
            toastMessage(context.getString(R.string.capture_image_failed) + res.error);
            return false;
        }
        FingerprintImage fi = (FingerprintImage) res.data;
        ZzLog.i(TAG, "Fingerprint image quality is " + Bione.getFingerprintQuality(fi));
        updateFingerprintImage(fi);

        res = Bione.extractFeature(fi);

        if (res.error != Bione.RESULT_OK) {
            toastMessage(context.getString(R.string.enroll_failed_because_of_extract_feature) + res.error);
            return false;
        }
        fpFeat = (byte[]) res.data;

        int i = Bione.identify(fpFeat);
        if (i >= 0) {
            Toast.makeText(context, "aready in database id = " + i, 
                    Toast.LENGTH_LONG).show();
            fingerprintImage = fi;
            fingerprintImageID = i;
            ZzLog.i(TAG, "aready in database id = " + i);
            return true;
        } else {
            Toast.makeText(context, "ready to save database id = " + i, 
                    Toast.LENGTH_LONG).show();
            ZzLog.i(TAG, "ready to save database id = " + i);
        }

        res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat);
        long generalizeTime = System.currentTimeMillis() - startTime;
        if (res.error != Bione.RESULT_OK) {
            toastMessage(context.getString(R.string.enroll_failed_because_of_make_template) + res.error);

            return false;
        }
        byte[] fpTemp =  (byte[]) res.data;
        int id = Bione.getFreeID();
        if (id < 0) {
            toastMessage(context.getString(R.string.enroll_failed_because_of_get_id) + id);
            return false;
        }

        int ret = Bione.enroll(id, fpTemp);
        if (ret != Bione.RESULT_OK) {
            toastMessage(context.getString(R.string.enroll_failed_because_of_error) + ret);
            return false;
        }

        toastMessage(context.getString(R.string.enroll_success) + id);
        fingerprintImage = fi;
        fingerprintImageID = id;
        return true;
    }

    private void updateFingerprintImage(FingerprintImage fi) {
        if (mFingerprintImageView == null) {
            return;
        }
        byte[] fpBmp = null;
        Bitmap bitmap = null;
        if (fi != null && (fpBmp = fi.convert2Bmp()) != null && 
                (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) != null) {
            mFingerprintImageView.setImageBitmap(bitmap);
        } else {
            mFingerprintImageView.setImageResource(R.drawable.nofinger);
        }
    }

    /**
     * read fingerprint
     */
    public FingerprintImage capture() {

        toastMessage("capture....");

        FingerprintImage fi = null;
        if (mDeviceOpened && isUsbGranted) {
            if (! isScanGranted2) {
                initScanner();
                return null;
            }
            toastMessage("mDeviceOpened ...");

            mScanner.prepare();

            toastMessage("mDeviceOpened ...prepared");
            Result res = mScanner.capture();
            mScanner.finish();
            if (res.error != FingerprintScanner.RESULT_OK) {
                toastMessage("can not get fingerprint data");
                return null;
            }

            fi = (FingerprintImage) res.data;
            String log = "Fingerprint image quality is " + Bione.getFingerprintQuality(fi);
            ZzLog.i(TAG, log);
            toastMessage(log);

        } else {
            toastMessage("can not open fingerprint device.");
            if(!isUsbGranted) {
                toastMessage("isScanGranted == false");
            }
        }

        return fi;
    }
    
    public boolean capture(ImageView view) {

        fingerprintImage = capture();
        mFingerprintImageView = view;

        if (fingerprintImage != null) {
            updateFingerprintImage(fingerprintImage);
            return true;
        }
        return false;
    }

    private void toastMessage(String message) {
//        Toast.makeText(context, "Fingerprint device: " 
//                    + message, Toast.LENGTH_SHORT).show();
        ZzLog.i(TAG, message);
    }
    /**
     * 打开设备
     */
    public boolean openDevice() {
        if (mDeviceOpened) {
            return mDeviceOpened;
        }
        int error;
        if (mScanner == null) {
            initScanner();
        }
        if ((error = mScanner.open()) != FingerprintScanner.RESULT_OK) {
            toastMessage(context.getString(R.string.fingerprint_device_open_failed)
                    + error);
            mDeviceOpened = false;
            return false;
        } else {
            toastMessage(context.getString(R.string.fingerprint_device_open_success));
        }
        
        if ((error = Bione.initialize(context, FP_DB_PATH)) != Bione.RESULT_OK) {
            toastMessage(context.getString(R.string.algorithm_initialization_failed)
                    + error);
            mDeviceOpened = false;
            return false;
        }

        boolean isInit = SharePrefUtil.getBoolean(context, "init", true);

        toastMessage("isInit = ." + isInit);
        if (isInit) {
            SharePrefUtil.saveBoolean(context, "init", false);
            toastMessage("clear bione database.");
            Bione.clear();
        }

        toastMessage("open device ok.");
        mDeviceOpened = true;
        return mDeviceOpened;
    }

    /**
     * 关闭设备
     */
    public void closeDevice() {

        int error;
        if ((error = mScanner.close()) != FingerprintScanner.RESULT_OK) {
            // Toast.makeText(this, getString(R.string.fingerprint_device_close_failed) + error, Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(this, getString(R.string.fingerprint_device_close_success), Toast.LENGTH_SHORT).show();
        }
        //if ((error = Bione.exit()) != Bione.RESULT_OK) {
        //    Toast.makeText(this, getString(R.string.algorithm_cleanup_failed) + error, Toast.LENGTH_SHORT).show();
        //}
        mDeviceOpened = false;
    }

    public String featureToBase64(byte[] feature) {
        String base64String = Base64.encodeToString(feature, Base64.DEFAULT);
        return base64String;
    }

    public byte[] base64ToFeature(String src) {
        byte[] raw = null;
        if (src != null) {
            raw = Base64.decode(src, Base64.DEFAULT);
        }
        return raw;
    }

    public byte[] getFpFeat() {
        return fpFeat;
    }

    public void setFpFeat(byte[] fpFeat) {
        this.fpFeat = fpFeat;
    }
    
    public void delete(int id) {
        int i = Bione.delete(id);
    }
}
