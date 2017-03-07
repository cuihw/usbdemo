package com.xdja.usbdemo.ui;

import com.smartshell.common.ZzLog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class Dlg {

    private static ProgressDialog dialog;

    public static void show(Context context, String message) {
        dialog = ProgressDialog.show(context, "Caption:", message);
    }

    public static void dismiss() {
        if (dialog!= null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        ZzLog.i("ProgressDlg", message);
    }
    
    public static void toast(Context context, String tag, String message) {
        Toast.makeText(context, tag + ": " + message, Toast.LENGTH_SHORT).show();
        ZzLog.i(tag, message);
    }
    
    public static void alert(Context context, String title, String message) {
        new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Confirm",  null).show();        
    }

}
