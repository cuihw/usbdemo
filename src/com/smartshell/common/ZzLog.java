package com.smartshell.common;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ZzLog {
    private static String MYLOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/xdja/";
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");
    // 日志的输出格式
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 打开日志文件并写入日志
     * @return
     **/
    public static void i(String tag, String msg) {
        Log.i(tag, msg);
        msg = msg + "\r\n";
        writeLogtoFile(null, tag, msg);
    }

    public static void LOG(Exception ex, String logType, String tag) {
        String msg = "";
        String exString = ex.toString();
        if (!TextUtils.isEmpty(exString)) {
            msg = msg + exString + "\n";
        }
        String exMsg = ex.getMessage();
        if (!TextUtils.isEmpty(exMsg)) {
            msg = msg + exMsg + "\n";
        }
        StackTraceElement[] errs = ex.getStackTrace();
        for (StackTraceElement element : errs) {
            msg = msg + "  at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() + ")\n";
        }
        writeLogtoFile(logType, tag, msg);
    }

    private static void writeLogtoFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
        Date nowtime = new Date();
        String needWriteMessage = myLogSdf.format(nowtime) + " " + mylogtype
            + "|" + tag + ":" + text;
        createDir(MYLOG_PATH_SDCARD_DIR);
        File logFile = new File(MYLOG_PATH_SDCARD_DIR, "usb.log.txt");
        try {
            FileWriter filerWriter = new FileWriter(logFile, true);//后面这个参数代表是不是要
            // 接上文件中原来的数据，
            // 不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createDir(String destPath) {
        File dir = new File(destPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}