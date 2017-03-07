package com.xdja.usbdemo.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;


public class UsbReceiver extends BroadcastReceiver
{
	public static final int USB_STATE_ON = 0x21;
	public static final int USB_STATE_OFF = 0x22;
	public IntentFilter filter = new IntentFilter();

	private final Context context;
	private final Handler handler;

	public UsbReceiver(Context context,Handler handler) {
		this.context = context;
		this.handler = handler;
		filter.addAction(Intent.ACTION_MEDIA_CHECKING);
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addDataScheme("file");
	}
	public Intent registerReceiver() {
		return this.context.registerReceiver(this, filter);
	}
	public void unregisterReceiver() {
		this.context.unregisterReceiver(this);
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		if (this.handler == null) {
			return;
		}
		Message msg = new Message();
		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)
				|| intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
			msg.arg1 = USB_STATE_ON;
		} else {
			msg.arg1 = USB_STATE_OFF;
		}
		this.handler.sendMessage(msg);
	};
}