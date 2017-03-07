package com.xdja.usbdemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Guojie on 16/7/26.
 */

public class Base64Utils {

    /**
     * bitmap转为base64
     * 
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     * 
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String base64ToString(String src) {
        byte[] bytes = Base64.decode(src, Base64.DEFAULT);
        return new String(bytes);
    }

    public static String stringToBase64(String src) {
        byte[] input = src.getBytes();
        String des = Base64.encodeToString(input, Base64.DEFAULT);
        return des;
    }
    
    public static String byteToBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
}
