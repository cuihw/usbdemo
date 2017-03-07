package com.xdja.usbdemo.ori;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;


public class Usb extends BroadcastReceiver 
{
	//MainActivity execactivity;
	Handler handler;
	Context context;
	public static final int USB_STATE_ON = 0x21;
	public static final int USB_STATE_OFF = 0x22;
	public IntentFilter filter = new IntentFilter();
	public Usb(Handler handler, Context context) {
		//execactivity = (MainActivity) context;
	    this.context = context;
		this.handler = handler;
		filter.addAction(Intent.ACTION_MEDIA_CHECKING);
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addDataScheme("file");
	}
	public Intent registerReceiver() {
		return context.registerReceiver(this, filter);
	}
	public void unregisterReceiver() {
	    context.unregisterReceiver(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (handler == null) {
			return;
		}
		Message msg = handler.obtainMessage();
		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)
				|| intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
			msg.arg1 = USB_STATE_ON;
		} else {
			msg.arg1 = USB_STATE_OFF;
		}
		handler.sendMessage(msg);
	};
}