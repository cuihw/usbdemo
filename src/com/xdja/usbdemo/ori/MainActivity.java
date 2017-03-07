package com.xdja.usbdemo.ori;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xdja.usbdemo.R;
import com.xdja.usbdemo.R.id;
import com.xdja.usbdemo.R.layout;
import com.xdja.usbdemo.R.menu;
import com.viewtool.USBDriver.ErrorType;
import com.viewtool.USBDriver.UsbDriver;

public class MainActivity extends Activity {
	StringBuffer mStringBuffer_Console_Text = new StringBuffer("Show Info:\n");

	// 权限
	private static final String ACTION_USB_PERMISSION = "com.smartshell.usbdemo.USB_PERMISSION";

	private static final int REQUEST_CODE_ZHIWEN = 1;

	private static final int REQUEST_CODE_SHENFENZHENG = 2;
	// 接口类
	UsbDriver mUsbDriver;
	UsbManager mUsbManager;
	UsbDevice mUsbDevice;
	// 界面控件
	PendingIntent pendingIntent;

	Button mButtonStar;
	Button mButtonReset;
	TextView mTextView_ShowConsole;
	// usb监听
	public MyHandler mHandler;
	Usb usbstates;
	class MyHandler extends Handler {


		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == Usb.USB_STATE_ON) 
			{

				reset_usb();
				appendConsole("USB 已连接.");
			} else if (msg.arg1 == Usb.USB_STATE_OFF) {

				release_usb();
				appendConsole("USB 已断开.");
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new MyHandler();
		usbstates = new Usb(mHandler, this);
		usbstates.registerReceiver();

		///////////////
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		registerReceiver(mUsbReceiver, filter);

		mButtonStar = (Button) findViewById(R.id.btn_Star);
		mButtonReset = (Button) findViewById(R.id.btn_reset);
		mTextView_ShowConsole = (TextView) findViewById(R.id.ShowConsole);
		set_Listener();

		findViewById(R.id.btn_Aratek).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Long lastTime=(Long)v.getTag();
				if (null==lastTime||System.currentTimeMillis()-lastTime>1000)
				{
					v.setTag(System.currentTimeMillis());
					startAratek();
				}
			}
		});

		findViewById(R.id.btn_CVR).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Long lastTime=(Long)v.getTag();
				if (null==lastTime||System.currentTimeMillis()-lastTime>1000)
				{
					v.setTag(System.currentTimeMillis());
					startCVR();
				}
			}
		});

	}

	private void set_Listener() {
		mButtonStar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				config_usb(true);
			}
		});

		mButtonReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				release_usb();
				reset_usb();
			}
		});
	}

	/**
	 * 配置usb连接
	 */
	private boolean config_usb(boolean showConsole) 
	{
		if (null==mUsbDriver||mUsbDevice==null)
		{
			mUsbManager = (UsbManager) getSystemService(MainActivity.USB_SERVICE);
			pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
			mUsbDriver = new UsbDriver(mUsbManager, pendingIntent);
			mUsbDevice = mUsbDriver.ScanDevices();
		}

		if (mUsbDevice != null) 
		{
			if (showConsole)
			{
				appendConsole("已找到设备.");
			}
			return true;

		} else {
			if (showConsole)
			{
				appendConsole("未找到设备.");
			}
			return false;
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						System.out.println("Authorization");
						if (mUsbDevice != null) {
						    
						}
					} else {
						System.out.println("不给权限");
						return;
					}
				}
			}
		}
	};

	/**
	 * 启动指纹采集页面
	 */
	private void startAratek() {
		if (openZhiwen()) {
			Intent intent=new Intent();
			intent.setClass(this, ActAratekFinger.class);
			startActivityForResult(intent,REQUEST_CODE_ZHIWEN);
		}
	}

	/**
	 * 启动CVR页面
	 */
	private void startCVR() {
		if (openIDCard()) {
			Intent intent=new Intent();
			intent.setClass(this,ActIDCard.class);
			startActivityForResult(intent,REQUEST_CODE_SHENFENZHENG);
		}
	}


	private byte[] sendCmd(byte[] cmd,String startDesc,String sendSuccessDesc) {
		int ret;
		if (mUsbDevice == null) {
			return null;
		}
		ret = mUsbDriver.OpenDevice();
		if (ret != ErrorType.ERR_SUCCESS) {
			return null;
		} else {
			//Open Device success;
			byte[] WriteData = cmd;
			if (!TextUtils.isEmpty(startDesc))
			{
				appendConsole("--  "+startDesc+"  --");
			}

			int i_USBWriteData_ep1 = mUsbDriver.USBWriteData(Config.EP1_OUT,
					WriteData, WriteData.length, 1000);
			if (i_USBWriteData_ep1 != WriteData.length) {
				appendConsole("USBWriteData error.");
			} else {
				if (!TextUtils.isEmpty(sendSuccessDesc))
				{
					appendConsole("--  "+sendSuccessDesc+"  --");
				}
			}

			byte[] readData = new byte[64];
			//读取返回
			int i_USBReadData_ep1 = mUsbDriver.USBReadData(Config.EP1_IN, readData,64, 1000);
			return readData;
		}
	}

	private boolean openIDCard()
	{
		if (false==config_usb(false))
		{
			appendConsole("身份证打开失败:未连接设备.");
			return false;
		}
		byte[] cmd=new byte[]{0x0A,0x13,0x01,0x00,0x1E,0x0B};
		byte[] ret=sendCmd(cmd,"" , "已发送打开身份证模块供电指令");
		if (ret!=null&&ret[0]==((byte)0x0B)&&ret[1]==((byte)0x00)&&ret[2]==((byte)0x00)&&ret[3]==((byte)0x0B)&&ret[4]==((byte)0x0A))
		{
			appendConsole("身份证打开成功.");
			return true;
		}
		else
		{
			appendConsole("身份证打开失败.");
			return false;
		}
	}
	private void closeIDCard()
	{
		byte[] cmd=new byte[]{0x0A,0x13,0x01,0x01,0x1F,0x0B};
		byte[] ret=sendCmd(cmd,"" , "已发送关闭身份证模块供电指令");
		if (ret!=null&&ret[0]==((byte)0x0B)&&ret[1]==((byte)0x00)&&ret[2]==((byte)0x00)&&ret[3]==((byte)0x0B)&&ret[4]==((byte)0x0A))
		{
			appendConsole("身份证关闭成功.");
		} else {
			appendConsole("身份证关闭失败.");
		}
	}

	private boolean openZhiwen()
	{
		if (false==config_usb(false))
		{
			appendConsole("指纹打开失败,未连接设备.");
			return false;
		}
		byte[] cmd=new byte[] { 0x0A,0x15,0x01,0x00,0x20,0x0B };
		byte[] ret = sendCmd(cmd, "" , null); // 供电
		if (ret!=null&&
		        ret[0]==((byte)0x0B)&&
		        ret[1]==((byte)0x00)&&
		        ret[2]==((byte)0x00)&&
		        ret[3]==((byte)0x0B)&&
		        ret[4]==((byte)0x0A)) {
			appendConsole("指纹打开成功.");
			return true;
		} else {
			appendConsole("指纹打开失败.");
			return false;
		}
	}
	
	/**
	 * 关闭指纹模块
	 */
	private void closeZhiwen()
	{
		byte[] cmd=new byte[]{0x0A,0x15,0x01,0x01,0x21,0x0B};
		byte[] ret=sendCmd(cmd,"" , null);
		if (ret!=null&&
		        ret[0]==((byte)0x0B)&&
		        ret[1]==((byte)0x00)&&
		        ret[2]==((byte)0x00)&&
		        ret[3]==((byte)0x0B)&&
		        ret[4]==((byte)0x0A)) {
			appendConsole("指纹关闭成功.");
		}
		else
		{
			appendConsole("指纹关闭失败.");
		}

	}

	/**
	 * 打印输出到屏幕
	 * @param text
	 */
	private void appendConsole(String text)
	{
		mStringBuffer_Console_Text.insert(0, text+"\n");
		mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_SHENFENZHENG:
			closeIDCard();
			break;
		case REQUEST_CODE_ZHIWEN:
			closeZhiwen();
		default:
			break;
		}
	}

	/**
	 * 配置usb
	 */
	private boolean reset_usb() {
		mUsbManager = (UsbManager) getSystemService(MainActivity.USB_SERVICE);
		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		mUsbDriver = new UsbDriver(mUsbManager, pendingIntent);
		mUsbDevice = mUsbDriver.ScanDevices();

		if (mUsbDevice != null) 
		{
			appendConsole("已找到设备.");
			return true;
		} else 
		{
			appendConsole("未找到设备.");
			return false;
		}
	}
	
	/**释放*/
	private void release_usb()
	{
		mUsbDriver = null;
		mUsbDevice = null;
	}
}
