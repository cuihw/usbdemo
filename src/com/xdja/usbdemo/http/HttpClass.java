package com.xdja.usbdemo.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.xdja.usbdemo.bean.PersonBean;

/**
 */

public class HttpClass {
    private static final String TAG = "HttpClass";

    private static final String REQUEST_ADDRESS = "http://192.168.22.242:7080/rsafinger/";
    private static final String REQUEST_ADDRESS_1
               = "http://192.168.22.242:7080/rsafinger/findAllPersons.do";
    public interface RequestListener {
        public void onResponse(String response);
    }

    public static void savePerson(PersonBean person, final RequestListener listener) {
        Gson gson = new Gson();
        String json = gson.toJson(person);
        String url = HttpClass.REQUEST_ADDRESS + "savePerson.do";
        HttpClass.startRequest(url, json,listener);
    }

    public static void findPerson(String id, final RequestListener listener) {
        String url = HttpClass.REQUEST_ADDRESS + "findPerson.do";
        HttpClass.startRequest(url, "id=" + id, listener);
    }

    public static void findAllPersons(final RequestListener listener) {
        String url = HttpClass.REQUEST_ADDRESS + "findAllPersons.do";
        HttpClass.startRequest(url, "", listener);
    }

    public static void findAllFingerprint(final RequestListener listener) {
        String url = HttpClass.REQUEST_ADDRESS + "findAllFingerprint.do";
        HttpClass.startRequest(url, "", listener);
    }

    public static void deletePerson(String id, final RequestListener listener) {
        String url = HttpClass.REQUEST_ADDRESS + "deletePerson.do";
        HttpClass.startRequest(url, "id=" + id, listener);
    }

    // 
    public static void findFingerprint(String id, final RequestListener listener) {
        String url = HttpClass.REQUEST_ADDRESS + "findFingerprint.do";
        HttpClass.startRequest(url, "id=" + id, listener);
    }

    public static void startRequest(final String path, final JSONObject json, final RequestListener
            listener) {
        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = jsonPost(path, json);
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        });
        requestThread.start();
    }

    public static void startRequest(final String path, final String parameter, final RequestListener
            listener) {
        
        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = post(path, parameter);
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        });
        requestThread.start();
    }

    protected static String post(String path, String parameter) {
        BufferedReader in = null;
        String result = "";
        OutputStream os = null;

        URL url = null;
        try {
            url = new URL(path);
            Log.i(TAG, "parameter = " + parameter);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");
            conn.setRequestProperty("Content-Type", "application/json");
            os = conn.getOutputStream();
            os.write(parameter.getBytes());
            os.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            if (conn.getResponseCode() == 200) {
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    private static String jsonPost(final String path, final JSONObject json) {
        String result = "";
        String parameter = String.valueOf(json);
        Log.i(TAG, "content = " + parameter);
        result = post(path, parameter);
        return result;
    }

    public static Bitmap getPic(String filename, int w, int h) {
        Bitmap bitmap = BitmapFactory.decodeFile(filename);
        bitmap = cutPicture(bitmap, w, h);
        return bitmap;
    }

    public static Bitmap cutPicture(Bitmap bitmap, int w, int h) {
        int wid = bitmap.getWidth();
        int hei = bitmap.getHeight();
        if (wid > w) {
            hei = w * hei / wid;
            wid = w;
            bitmap = Bitmap.createScaledBitmap(bitmap, wid, hei, true);
        }
        if (hei > h) {
            int top = (hei - h) / 2;
            hei = h;
            bitmap = Bitmap.createBitmap(bitmap, 0, top, wid, hei);
        }
        return bitmap;
    }


}
