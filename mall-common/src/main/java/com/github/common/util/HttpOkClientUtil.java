package com.github.common.util;

import com.github.common.json.JsonUtil;
import com.google.common.io.Files;
import com.squareup.okhttp.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpOkClientUtil {

    private static final MediaType PNG = MediaType.parse("image/png");
    private static final MediaType JPG = MediaType.parse("image/jpeg");
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //private static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded");
    private static final MediaType FORM = MediaType.parse("multipart/form-data");

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    static {
        HTTP_CLIENT.setConnectTimeout(10, TimeUnit.SECONDS);
    }

    public static String getWithHeader(String url, Map<String, Object> header) {
        Request.Builder builder = new Request.Builder();
        for (Map.Entry<String, Object> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue().toString());
        }
        return handle(url, builder);
    }

    public static String postWithHeader(String url, Map<String, Object> header, Map<String, Object> params) {
        Request.Builder builder = new Request.Builder().post(RequestBody.create(JSON, JsonUtil.toJson(params)));
        for (Map.Entry<String, Object> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue().toString());
        }
        return handle(url, builder);
    }

    public static String get(String url) {
        return get(url, null);
    }

    public static String get(String url, Map<String, Object> params) {
        // get 请求是把参数跟在后面
        if (params != null && params.size() > 0) {
            url = U.appendUrl(url) + U.formatParam(params);
        }
        return handle(url, new Request.Builder());
    }

    public static String postWithJson(String url, Map<String, Object> params) {
        return handle(url, new Request.Builder().post(RequestBody.create(JSON, JsonUtil.toJson(params))));
    }

    public static String postWithForm(String url, String params) {
        return handle(url, new Request.Builder().post(RequestBody.create(FORM, params)));
    }

    /** 向指定 url 上传 png 图片文件 */
    public static String upload(String url, Map<String, File> files, Map<String, String> params) {
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, File> entry : files.entrySet()) {
            File file = entry.getValue();
            MediaType type = null;
            if (file.getName().endsWith(".png")) {
                type = PNG;
            } else if (file.getName().endsWith(".jpg")) {
                type = JPG;
            }
            if (type != null) {
                RequestBody body = RequestBody.create(type, file);
                builder.addFormDataPart(entry.getKey(), null, body);
            }
        }
        return handle(url, new Request.Builder().post(builder.build()));
    }

    /** 下载 url 到指定的文件 */
    public static void download(String url, String file) throws IOException {
        ResponseBody response = response(url, new Request.Builder());
        if (response != null) {
            Files.write(response.bytes(), new File(file));
        }
    }

    private static String handle(String url, Request.Builder builder) {
        ResponseBody response = response(url, builder);
        String body = "";
        if (response != null) {
            try {
                body = response.string();
            } catch (IOException e) {
                if (LogUtil.ROOT_LOG.isWarnEnabled()) {
                    LogUtil.ROOT_LOG.warn("response ({}) exception", response.toString(), e);
                }
            }
        }
        // if (LogUtil.ROOT_LOG.isDebugEnabled())
        //    LogUtil.ROOT_LOG.debug("request ({}), return ({})", url, body);
        return body;
    }
    private static ResponseBody response(String url, Request.Builder builder) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        Request request = builder.url(url).build();
        try {
            return HTTP_CLIENT.newCall(request).execute().body();
        } catch (Exception e) {
            if (LogUtil.ROOT_LOG.isWarnEnabled()) {
                LogUtil.ROOT_LOG.warn("request ({}) exception", request.toString(), e);
            }
        }
        return null;
    }

}
