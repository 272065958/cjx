package com.model.cjx.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.model.cjx.activity.BaseActivity;
import com.model.cjx.util.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by cjx on 2016/6/21.
 * 访问网络的类
 */
public class HttpUtils {
    private static HttpUtils instance;
    private OkHttpClient client;
    private String serverApiUri;
    private MyCookieJar cookieJar;

    private class MyCookieJar implements CookieJar {
        private final ArrayList<Cookie> cookieStore = new ArrayList<>();

        @Override
        public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            //当获得一个Response时，会调用这个方法来存储Cookie
            cookieStore.addAll(list);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl httpUrl) {
            //当要call一个Request时，会调用这个方法来为请求的head添加cookie
            List<Cookie> cookies = cookieStore;
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }

        public void clearCookie() {
            if (cookieStore != null) {
                cookieStore.clear();
            }
        }
    }

    private HttpUtils() {
        cookieJar = new MyCookieJar();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        client = builder.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cookieJar(cookieJar).build();
    }

    public static HttpUtils getInstance() {
        if (instance == null) {
            synchronized (HttpUtils.class) {
                if (instance == null) {
                    instance = new HttpUtils();
                }
            }
        }
        return instance;
    }

    // 清除登录cookid
    public void clearCookie() {
        cookieJar.clearCookie();
    }

    public void setServerApiUri(String url) {
        serverApiUri = url;
    }

    public String getServerApiUri() {
        return serverApiUri;
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    // 发起post请求
    public void postEnqueue(Callback callback, String action, String... params) {
        Request request = getRequest(serverApiUri + action, getFormBody(params));
        enqueue(callback, request);
    }

    // 发起请求
    public void enqueue(Callback callback, Request request) {
        client.newCall(request).enqueue(callback);
    }

    // 获取一个request
    private Request getRequest(String url, RequestBody body) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).post(body).build();
        return request;
    }


    private static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    /**
     * 获取一个请求的参数body
     *
     * @param params
     * @return
     */
    private RequestBody getFormBody(String... params) {
//        RequestBody body = null;    // 坑，  后台神奇的读取编码形式，无法识别中文
//        if (params != null) {
//            int length = params.length;
//            if (length % 2 == 0) {
//                FormBody.Builder builder = new FormBody.Builder();
//                int count = length / 2;
//                for (int i = 0; i < count; i++) {
//                    String value = params[i * 2 + 1];
//                    if (!TextUtils.isEmpty(value)) {
//                        builder.add(params[i * 2], value);
//                        Log.e("TAG", params[i * 2] + " = " + value);
//                    }
//                }
//                String token = MyApplication.getInstance().token;
//                if (!TextUtils.isEmpty(token)) {
//                    builder.add("token", token);
//                    Log.e("TAG", "token = " + token);
//                }
//                body = builder.build();
//            } else {
//                Log.e("TAG", "======== > params error");
//            }
//        }
        StringBuilder sb = null;
        if (params != null) {
            int length = params.length;
            if (length % 2 == 0) {
                sb = new StringBuilder();
                int count = length / 2;
                for (int i = 0; i < count; i++) {
                    String value = params[i * 2 + 1];
                    if (!TextUtils.isEmpty(value)) {
                        sb.append(params[i * 2]);
                        sb.append("=");
                        sb.append(value);
                        sb.append("&");
                    }
                }
//                String token = MyApplication.getInstance().token;   坑  如果所有接口都传入token，后台会给你神奇的惊喜，所以不要默认都传token
//                if (!TextUtils.isEmpty(token)) {
//                    sb.append("token=");
//                    sb.append(token);
//                } else {
                    int leng = sb.length();
                    if(leng > 0){
                        sb.deleteCharAt(leng-1);
                    }
//                }
            } else {
                Log.e("TAG", "======== > params error");
            }
        }
        String param = sb == null ? null : sb.toString();
        Log.e("TAG", "param = "+param);
        return RequestBody.create(FORM_CONTENT_TYPE, param);
    }

    // 创建一个文件上传请求
    private RequestBody getMultipartBody(ArrayList<String> path, String... params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (params != null) {
            int length = params.length;
            if (length % 2 == 0) {
                int count = length / 2;
                for (int i = 0; i < count; i++) {
                    String value = params[i * 2 + 1];
                    if (!TextUtils.isEmpty(value)) {
                        builder.addFormDataPart(params[i * 2], value);
                        Log.e("TAG", params[i * 2] + " = " + value);
                    }
                }
            } else {
                Log.e("TAG", "======== > params error");
            }
        }
        if (!path.isEmpty()) {
            int i = 0;
            for (String str : path) {
                File f = new File(str);
                if (f.exists()) {
                    // 设置上传文件为  图片
                    builder.addFormDataPart("file_" + (i++), f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
                    Log.e("TAG", "file_" + i + " path = " + str);
                } else {
                    Log.e("TAG", "file_" + i + " no exist,  path = " + str);
                }
            }
        }
        return builder.build();
    }

    // 创建一个下载
    public Call download(String url, final ProgressResponseListener listener, Callback callback) {
        //构造请求
        final Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient downloadClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        ProgressResponseBody body = new ProgressResponseBody(originalResponse.body(), listener);
                        return originalResponse.newBuilder().body(body).build();
                    }
                })
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                //其他配置
                .build();
        Call call = downloadClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

    // 创建一个上传
    public Call upload(BaseActivity activity, ProgressRequestListener listener,
                       Callback callback, ArrayList<String> path, String action, String... params) {
        saveApiConnectToFile(activity, action, path, params);
        //构造上传请求，类似web表单
        RequestBody requestBody = getMultipartBody(path, params);
        final Request request = getRequest(serverApiUri + action, new ProgressRequestBody(requestBody, listener));
        return upload(callback, request);
    }

    private Call upload(Callback callback, Request request) {
        Call call = null;
        try {
            call = client.newCall(request);
            call.enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return call;
    }

    private final String API_FILE = "api_connect";

    private void saveApiConnectToFile(Context context, String action, ArrayList<String> path, String... params) {
        StringBuilder sb = new StringBuilder();
        sb.append("action = ");
        sb.append(action);
        if (path != null) {
            sb.append(", file = ");
            for (String p : path) {
                sb.append(p);
                sb.append(" ++ ");
            }
        }
        if (params != null) {
            sb.append(", params = ");
            for (String p : params) {
                sb.append(p);
                sb.append(" ++ ");
            }
        }
        Tools.saveToFile(context, API_FILE, sb.toString());
    }

}
