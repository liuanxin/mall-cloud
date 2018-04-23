package com.github.common.http;

import com.github.common.util.A;
import com.github.common.util.LogUtil;
import com.github.common.util.U;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtil {

    private static final int TIME_OUT = 3 * 1000;
    private static final String GET = "GET";
    private static final String POST = "POST";

    public static String get(String url) {
        return get(url, null);
    }
    public static String get(String url, Map<String, Object> params) {
        return get(url, params, TIME_OUT);
    }
    public static String get(String url, int timeout) {
        return get(url, null, timeout);
    }
    public static String get(String url, Map<String, Object> params, int timeout) {
        // get 请求是把参数跟在后面
        if (params != null && params.size() > 0) {
            url += (U.checkRegexWithRelax(url, "\\?") ? "&" : "?") + U.formatParam(params);
        }
        return connection(url, GET, null, timeout);
    }

    public static String post(String url, Map<String, Object> params) {
        return post(url, params, TIME_OUT);
    }
    public static String post(String url, Map<String, Object> params, int timeout) {
        return connection(url, POST, params, timeout);
    }

    /** 下载 url 到指定的文件 */
    public static void download(String url, String file) throws IOException {
        InputStream inputStream = response((HttpURLConnection) new URL(url).openConnection());
        if (inputStream != null) {
            ByteStreams.copy(inputStream, new FileOutputStream(file));
        }
    }
    /** 删除指定的文件 */
    public static void delete(String file) throws IOException {
        if (!new File(file).delete()) {
            if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                LogUtil.ROOT_LOG.error("无法删除({})", file);
            }
        }
    }

    private static InputStream response(HttpURLConnection conn) {
        try {
            return conn.getInputStream();
        } catch (IOException e) {
            return conn.getErrorStream();
        }
    }
    private static String connection(String url, String method, Map<String, Object> params, int timeout) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(timeout);
            connection.setConnectTimeout(timeout);
            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (A.isNotEmpty(params)) {
                Writer writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
                writer.write(U.formatParam(params));
                writer.flush();
                writer.close();
            }

            InputStream inputStream = response(connection);
            if (inputStream != null) {
                return CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            if (LogUtil.ROOT_LOG.isWarnEnabled()) {
                LogUtil.ROOT_LOG.warn("request ({}, {}) exception", url, method, e);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return U.EMPTY;
    }
}
