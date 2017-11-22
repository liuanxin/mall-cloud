package com.github.common.util;

import com.github.common.json.JsonResult;
import com.github.common.json.JsonUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

/** <span style="color:red;">!!!此工具类请只在 Controller 中调用!!!</span> */
public final class RequestUtils {

    private static final String USER_AGENT = "user-agent";
    private static final String REFERRER = "referer";

    /**
     * 获取真实客户端IP
     * 关于 X-Forwarded-For 参考: http://zh.wikipedia.org/wiki/X-Forwarded-For<br>
     * 这一 HTTP 头一般格式如下:
     * X-Forwarded-For: client1, proxy1, proxy2,<br><br>
     * 其中的值通过一个 逗号 + 空格 把多个 IP 地址区分开, 最左边(client1)是最原始客户端的IP地址,
     * 代理服务器每成功收到一个请求，就把请求来源IP地址添加到右边
     */
    public static String getRealIp() {
        HttpServletRequest request = getRequest();
        String ip = request.getHeader("X-Real-IP");
        if (U.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("X-Forwarded-For");
        if (U.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为 真实 ip
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (U.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (U.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("HTTP_CLIENT_IP");
        if (U.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("X-Cluster-Client-IP");
        if (U.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }

    /*** 是否是本机 */
    public static boolean isLocalRequest() {
        return U.isLocalRequest(getRealIp());
    }

    public static String userAgent() {
        return getRequest().getHeader(USER_AGENT);
    }

    /** 判断当前请求是否来自移动端, 来自移动端则返回 true */
    public static boolean isMobileRequest() {
        return U.checkMobile(userAgent());
    }

    /** 判断当前请求是否是 ajax 请求, 是 ajax 则返回 true */
    public static boolean isAjaxRequest() {
        HttpServletRequest request = getRequest();

        String requestedWith = request.getHeader("X-Requested-With");
        if (U.isNotBlank(requestedWith) && "XMLHttpRequest".equals(requestedWith)) {
            return true;
        }

        String contentType = request.getHeader("Content-Type");
        return (U.isNotBlank(contentType) && "application/json".startsWith(contentType))
                || U.isNotBlank(request.getParameter("_ajax"))
                || U.isNotBlank(request.getParameter("_json"));
    }

    /**
     * 格式化参数, 如果是文件流(form 表单中有 type="multipart/form-data" 这种), 则不打印出参数
     *
     * @return 示例: id=xxx&name=yyy
     */
    public static String formatParam() {
        // return getRequest().getQueryString(); // 没有时将会返回 null

        HttpServletRequest request = getRequest();
        String contentType = request.getContentType();
        boolean upload = U.isNotBlank(contentType) && contentType.startsWith("multipart/");
        return upload ? "uploading file" : U.formatParam(request.getParameterMap());
    }

    /** 返回 url 并且拼上参数, 非 get 请求将忽略参数 */
    public static String getUrl() {
        String url = getRequest().getRequestURL().toString();
        if ("get".equalsIgnoreCase(getRequest().getMethod())) {
            String param = formatParam();
            if (U.isNotBlank(param)) {
                url += ("?" + param);
            }
        }
        return url;
    }

    /**
     * 获取上一个请求的 url. 先从 requestUrl 读, 而再从 referrer 读.
     * 如果有值且值是以指定域名开头的, 且不能是主域名, 也不是要放过的 url, 就将此 url 转义了返回
     *
     * @param domain 主域名
     * @param letGoUrls 放过的 url 数组
     */
    public static String getLastUrl(String domain, String[] letGoUrls) {
        String last = getUrl();
        if (checkUrl(last, domain, letGoUrls)) {
            return U.urlEncode(last);
        }

        last = getReferrer();
        if (checkUrl(last, domain, letGoUrls)) {
            return U.urlEncode(last);
        }
        return U.EMPTY;
    }
    /** 返回的 url 不能为空, 跟主域名不一样, 且不包含在指定的 url 里面就返回 true */
    private static boolean checkUrl(String backUrl, String domain, String[] letGoUrls) {
        if (U.isNotBlank(backUrl)) {
            domain = U.addSuffix(domain);
            // 不能完全跟域名一样, 也不能在指定的 url 里面
            return !backUrl.equals(domain) && !letGo(backUrl, domain, letGoUrls);
        }
        return false;
    }
    /** 传入的 url 是在指定的里面就返回 true */
    private static boolean letGo(String backUrl, String domain, String[] letGoUrls) {
        domain = U.addSuffix(domain);
        for (String url : letGoUrls) {
            // url 里面有以 / 开头就去掉, domain 里面已经带了
            if (U.isNotBlank(url) && url.startsWith("/")) {
                url = url.substring(1);
            }
            if (backUrl.startsWith(domain + url)) {
                return true;
            }
        }
        return false;
    }

    /** 请求头里的 referer 这个单词拼写是错误的, 应该是 referrer, 然而历史遗留原因导致这个问题永远不会被更正 */
    public static String getReferrer() {
        return getRequest().getHeader(REFERRER);
    }

    /** 先从请求头中查, 为空再从参数中查 */
    public static String getHeaderOrParam(String param) {
        HttpServletRequest request = getRequest();
        String header = request.getHeader(param);
        if (U.isBlank(header)) {
            header = request.getParameter(param);
        }
        return U.isBlank(header) ? U.EMPTY : header.trim();
    }

    /** 格式化头里的参数: 键值以冒号分隔 */
    public static String formatHeadParam() {
        HttpServletRequest request = getRequest();

        StringBuilder sbd = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            sbd.append("<");
            String headName = headerNames.nextElement();
            sbd.append(headName).append(" : ").append(request.getHeader(headName));
            sbd.append(">");
        }
        return sbd.toString();
    }

    /** 将「json 字符」以 json 格式输出 */
    public static void toJson(JsonResult result, HttpServletResponse response) throws IOException {
        render("application/json", result, response);
    }
    private static void render(String type, JsonResult jsonResult, HttpServletResponse response) throws IOException {
        String result = JsonUtil.toJson(jsonResult);
        if (LogUtil.ROOT_LOG.isInfoEnabled()) {
            LogUtil.ROOT_LOG.info("return json: " + result);
        }
        response.setContentType(type + ";charset=UTF-8;");
        response.getWriter().write(result);
    }
    /** 将「json 字符」以 html 格式输出. 不常见! 这种只会在一些特殊的场景用到 */
    public static void toHtml(JsonResult result, HttpServletResponse response) throws IOException {
        render("text/html", result, response);
    }

    /** 基于请求上下文生成一个日志需要的上下文信息对象 */
    public static LogUtil.RequestLogContext logContextInfo(boolean online) {
        HttpServletRequest request = getRequest();

        return new LogUtil.RequestLogContext()
                .setOnline(online)
                .setIp(getRealIp())
                .setMethod(request.getMethod())
                .setUrl(request.getRequestURL().toString())
                .setParam(formatParam())
                .setHeadParam(formatHeadParam());
    }


    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    private static ServletRequestAttributes getRequestAttributes() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
    }
}
