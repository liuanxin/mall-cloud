package com.github.common.http;

import com.github.common.util.A;
import com.github.common.util.LogUtil;
import com.github.common.util.U;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    private static final int TIME_OUT = 20 * 1000;

    private static final int MAX_TOTAL = 200;
    private static final int MAX_PER_ROUTE = 40;

    private static final SSLConnectionSocketFactory SSL_CONNECTION_SOCKET_FACTORY;
    static {
        SSLContext ignoreVerifySSL = TrustAllCerts.SSL_CONTEXT;
        if (U.isBlank(ignoreVerifySSL)) {
            SSL_CONNECTION_SOCKET_FACTORY = SSLConnectionSocketFactory.getSocketFactory();
        } else {
            SSL_CONNECTION_SOCKET_FACTORY = new SSLConnectionSocketFactory(ignoreVerifySSL);
        }
    }

    private static void config(HttpRequestBase httpRequestBase) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(TIME_OUT)
                .setConnectTimeout(TIME_OUT)
                .setSocketTimeout(TIME_OUT).build();
        httpRequestBase.setConfig(requestConfig);
    }

    private static CloseableHttpClient createHttpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", SSL_CONNECTION_SOCKET_FACTORY)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        connectionManager.setMaxTotal(MAX_TOTAL);
        // 将每个路由基础的连接增加
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                // 如果已经重试了 5 次就不再重试
                if (executionCount >= 5) {
                    return false;
                }
                // 服务器丢掉了连接就重试
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                // SSL 握手异常时不重试
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }
                // 超时时不重试
                if (exception instanceof InterruptedIOException) {
                    return false;
                }
                // 目标服务器不可达时不重试
                if (exception instanceof UnknownHostException) {
                    return false;
                }
                // SSL 握手异常时不重试
                if (exception instanceof SSLException) {
                    return false;
                }

                HttpRequest request = HttpClientContext.adapt(context).getRequest();
                // 如果请求是幂等的就重试
                return !(request instanceof HttpEntityEnclosingRequest);
            }
        };

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setRetryHandler(httpRequestRetryHandler).build();
    }

    public static String get(String url) {
        url = urlHttp(url);
        HttpGet request = new HttpGet(url);
        return handle(request, null, null);
    }

    public static String get(String url, Map<String, Object> params) {
        url = urlGet(url, params);
        return get(url);
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

        HttpGet request = new HttpGet(url);
        handlerHeader(request, headerMap);
        return handle(request, U.formatParam(params), U.formatParam(headerMap));
    }

    public static String post(String url, Map<String, Object> params) {
        HttpPost request = handlerPostParams(url, params);
        return handle(request, U.formatParam(params), null);
    }

    public static String post(String url, String json) {
        HttpPost request = new HttpPost(url);
        request.setEntity(new ByteArrayEntity(json.getBytes(StandardCharsets.UTF_8)));
        return handle(request, json, null);
    }

    /** 向指定 url 进行 post 请求. 有参数和头 */
    public static String postWithHeader(String url, Map<String, Object> params, Map<String, Object> headers) {
        HttpPost request = handlerPostParams(url, params);
        handlerHeader(request, headers);
        return handle(request, U.formatParam(params), U.formatParam(headers));
    }

    public static String postFile(String url, Map<String, Object> params, Map<String, File> files) {
        HttpPost request = handlerPostParams(url, params);
        if (A.isEmpty(params)) {
            params = Maps.newHashMap();
        }
        if (A.isNotEmpty(files)) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setLaxMode();
            for (Map.Entry<String, File> entry : files.entrySet()) {
                String key = entry.getKey();
                File value = entry.getValue();

                entityBuilder.addBinaryBody(key, value);
                params.put(key, value.toString());
            }
            request.setEntity(entityBuilder.build());
        }
        return handle(request, U.formatParam(params), null);
    }

    /** 下载 url 到指定的文件 */
    public static void download(String url, String file) {
        url = urlHttp(url);
        HttpGet request = new HttpGet(url);
        config(request);
        try (CloseableHttpResponse response = createHttpClient().execute(request, HttpClientContext.create())) {
            response.getEntity().writeTo(new FileOutputStream(new File(file)));
        } catch (IOException e) {
            if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                LogUtil.ROOT_LOG.info("download ({}) exception", url, e);
            }
        }
    }

    private static HttpPost handlerPostParams(String url, Map<String, Object> params) {
        url = urlHttp(url);

        List<NameValuePair> nameValuePairs = Lists.newArrayList();
        if (A.isNotEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (U.isNotBlank(value)) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), value.toString()));
                }
            }
        }
        HttpPost request = new HttpPost(url);
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
        return request;
    }

    private static void handlerHeader(HttpRequestBase request, Map<String, Object> headers) {
        if (A.isNotEmpty(headers)) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                Object value = entry.getValue();
                if (U.isNotBlank(value)) {
                    request.addHeader(entry.getKey(), value.toString());
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

    private static String handle(HttpRequestBase request, String params, String headers) {
        config(request);

        String method = request.getMethod();
        String url = request.getURI().toString();

        long start = System.currentTimeMillis();
        try (CloseableHttpResponse response = createHttpClient().execute(request, HttpClientContext.create())) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                long ms = System.currentTimeMillis() - start;
                StringBuilder sbd = new StringBuilder();
                sbd.append("HttpClient => (").append(method).append(" ").append(url).append(")");
                if (U.isNotBlank(params)) {
                    sbd.append(" params(").append(params).append(")");
                }
                if (U.isNotBlank(headers)) {
                    sbd.append(" headers(").append(headers).append(")");
                }
                sbd.append(" time(").append(ms).append("ms), return(").append(result).append(")");
                Header[] allHeaders = response.getAllHeaders();
                if (A.isNotEmpty(allHeaders)) {
                    sbd.append(", response headers(");
                    for (Header header : allHeaders) {
                        sbd.append("<").append(header.getName()).append(" : ").append(header.getValue()).append(">");
                    }
                    sbd.append(")");
                }
                LogUtil.ROOT_LOG.info(sbd.toString());
            }
            return result;
        } catch (Exception e) {
            if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                LogUtil.ROOT_LOG.info("({} {}) exception", method, url, e);
            }
            return null;
        }
    }
}
