package com.github.common.util;

import com.github.common.json.JsonUtil;
import com.google.common.io.Files;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpOkClientUtil {

    private static final MediaType PNG = MediaType.parse("image/png");
    private static final MediaType JPG = MediaType.parse("image/jpeg");
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    // private static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded");

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    public static String get(String url) {
        return get(url, null);
    }

    public static String get(String url, Map<String, Object> params) {
        url = urlGet(url, params);
        return handle(url, new Request.Builder(), U.formatParam(params), null);
    }

    private static String urlGet(String url, Map<String, Object> params) {
        if (A.isNotEmpty(params)) {
            url = U.appendUrl(url) + U.formatParam(params);
        }
        return url;
    }

    /** 向指定 url 进行 get 请求. 有参数和头 */
    public static String getWithHeader(String url, Map<String, Object> params, Map<String, Object> headerMap) {
        url = urlGet(url, params);
        Request.Builder builder = new Request.Builder();
        handlerHeader(builder, headerMap);
        return handle(url, builder, null, U.formatParam(headerMap));
    }

    public static String post(String url, Map<String, Object> params) {
        String formatParam = U.formatParam(params);
        RequestBody body = RequestBody.create(MultipartBody.FORM, formatParam);
        Request.Builder builder = new Request.Builder().post(body);
        return handle(url, builder, formatParam, null);
    }

    public static String post(String url, String params) {
        RequestBody body = RequestBody.create(MultipartBody.FORM, params);
        Request.Builder builder = new Request.Builder().post(body);
        return handle(url, builder, params, null);
    }

    /** 向指定 url 进行 post 请求. 有参数和头 */
    public static String postWithHeader(String url, Map<String, Object> params, Map<String, Object> headers) {
        RequestBody body = RequestBody.create(MultipartBody.FORM, U.formatParam(params));
        Request.Builder builder = new Request.Builder().post(body);
        handlerHeader(builder, headers);
        return handle(url, builder, U.formatParam(params), U.formatParam(headers));
    }

    /** 向指定 url post 请求, 参数使用 json 方式 */
    public static String postWithJson(String url, Map<String, Object> params) {
        Request.Builder builder = new Request.Builder().post(RequestBody.create(JSON, JsonUtil.toJson(params)));
        return handle(url, builder, U.formatParam(params), null);
    }

    /** 向指定 url 上传 png 图片文件 */
    public static String postFile(String url, Map<String, Object> params, Map<String, File> files) {
        url = urlHttp(url);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (U.isNotBlank(value)) {
                builder.addFormDataPart(entry.getKey(), value.toString());
            }
        }
        for (Map.Entry<String, File> entry : files.entrySet()) {
            File file = entry.getValue();
            MediaType type = null;
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".png")) {
                type = PNG;
            } else if (fileName.endsWith(".jpg")) {
                type = JPG;
            }
            if (type != null) {
                RequestBody body = RequestBody.create(type, file);
                builder.addFormDataPart(entry.getKey(), null, body);
            }
        }
        return handle(url, new Request.Builder().post(builder.build()), U.formatParam(params), null);
    }

    /** 下载 url 到指定的文件 */
    public static void download(String url, String file) {
        url = urlHttp(url);
        Request request = new Request.Builder().url(url).build();
        try {
            ResponseBody response = HTTP_CLIENT.newCall(request).execute().body();
            if (response != null) {
                Files.write(response.bytes(), new File(file));
            }
        } catch (IOException e) {
            if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                LogUtil.ROOT_LOG.info("download ({}) exception", url, e);
            }
        }
    }

    private static void handlerHeader(Request.Builder request, Map<String, Object> headers) {
        if (A.isNotEmpty(headers)) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (U.isNotBlank(value)) {
                    request.addHeader(key, value.toString());
                }
            }
        }
    }

    private static String urlHttp(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    private static String handle(String url, Request.Builder builder, String params, String headers) {
        url = urlHttp(url);

        Request request = builder.url(url).build();
        String method = request.method();
        String requestUrl = request.url().toString();
        try {
            long start = System.currentTimeMillis();
            Response response = HTTP_CLIENT.newCall(request).execute();
            if (response != null) {
                ResponseBody body = response.body();
                if (body != null) {
                    String result = body.string();
                    if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                        long ms = System.currentTimeMillis() - start;
                        StringBuilder sbd = new StringBuilder();
                        sbd.append("OkHttp3 => (").append(method).append(" ").append(requestUrl).append(")");
                        if (U.isNotBlank(params)) {
                            sbd.append(" params(").append(params).append(")");
                        }
                        if (U.isNotBlank(headers)) {
                            sbd.append(" headers(").append(headers).append(")");
                        }
                        sbd.append(" time(").append(ms).append("ms), return(").append(result).append(")");
                        Headers h = response.headers();
                        if (U.isNotBlank(h)) {
                            sbd.append(", response headers(");
                            for (String name : h.names()) {
                                sbd.append("<").append(name).append(" : ").append(h.get(name)).append(">");
                            }
                            sbd.append(")");
                        }
                        LogUtil.ROOT_LOG.info(sbd.toString());
                    }
                    return result;
                }
            }
        } catch (IOException e) {
            if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                LogUtil.ROOT_LOG.info("({} {}) exception", method, requestUrl, e);
            }
        }
        return null;
    }
}
