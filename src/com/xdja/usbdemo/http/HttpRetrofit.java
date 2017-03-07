package com.xdja.usbdemo.http;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class HttpRetrofit {


    String baseUri = "http://192.168.22.242:7080/rsafinger/";

    private Context mContext;
    public static HttpRetrofit httpRetrofit;
    private int cacheSize = 10 * 1024 * 1024; // 10 MB
    private Cache cache;
    private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR;

    private File httpCacheDirectory;
    private OkHttpClient client;

    private HttpRetrofit(Context context) {
        this.mContext = context;
        initOkHttpCilent();
    }

    private void initOkHttpCilent() {
        if (client == null) {
            client = new OkHttpClient();
            httpCacheDirectory = new File(mContext.getCacheDir(), "httpCache");
            cache = new Cache(httpCacheDirectory, cacheSize);
            REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    if (FingerUtil.isNetWorkAvailable(mContext)) {
                        int maxAge = 60; // 在线缓存在1分钟内可读取
                        return originalResponse.newBuilder()
                                .removeHeader("Pragma")
                                .removeHeader("Cache-Control")
                                .header("Cache-Control", "public, max-age=" + maxAge)
                                .build();
                    } else {
                        int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                        return originalResponse.newBuilder()
                                .removeHeader("Pragma")
                                .removeHeader("Cache-Control")
                                .header("Cache-Control", "public, only-if-cached, max-stale="
                                        + maxStale)
                                .build();
                    }
                }
            };
            client.setCache(cache);
            client.interceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        }
    }

    public static HttpRetrofit getInstance(Context context) {
        if (httpRetrofit == null) {
            synchronized ( HttpRetrofit.class) {
                if (httpRetrofit == null) {
                    httpRetrofit = new  HttpRetrofit(context);
                }
            }
        }
        return httpRetrofit;
    }

    IAPIService apiService;

    private Object monitor = new Object();

    public IAPIService getApiService() {
        if (apiService == null) {
            synchronized (monitor) {
                if (apiService == null) {
                    apiService = new Retrofit.Builder()
                            .baseUrl(baseUri)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(IAPIService.class);
                }
            }
        }
        return apiService;
    }


}
