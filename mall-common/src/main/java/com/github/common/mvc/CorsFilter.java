package com.github.common.mvc;

import com.github.common.Const;
import com.github.common.util.A;
import com.github.common.util.U;
import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** 处理跨域 */
public class CorsFilter implements Filter {

    private static final String ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String METHODS = "Access-Control-Allow-Methods";
    private static final String HEADERS = "Access-Control-Allow-Headers";
    // /** for ie: https://www.lovelucy.info/ie-accept-third-party-cookie.html */
    // private static final String P3P = "P3P";

    private static void handlerCors(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if (U.isNotBlank(origin)) {
            if (U.isBlank(response.getHeader(ALLOW_ORIGIN))) {
                response.addHeader(ALLOW_ORIGIN, origin);
            }
            if (U.isBlank(response.getHeader(CREDENTIALS))) {
                response.addHeader(CREDENTIALS, "true");
            }
            if (U.isBlank(response.getHeader(METHODS))) {
                response.addHeader(METHODS, A.toStr(Const.SUPPORT_METHODS));
            }
            if (U.isBlank(response.getHeader(HEADERS))) {
                // 如果有自定义头, 附加进去, 避免用 *
                response.addHeader(HEADERS, "Accept, Accept-Encoding, Accept-Language, Cache-Control, " +
                        "Connection, Cookie, DNT, Host, User-Agent, Content-Type, Authorization, " +
                        "X-Requested-With, Origin, Access-Control-Request-headers");
            }
            /*
            if (RequestUtils.isIeRequest() && U.isBlank(response.getHeader(P3P))) {
                response.addHeader(P3P, "CP='CAO IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'");
            }
            */
        }
    }

    /** 在指定域名内的才加 cors 进 header */
    private static void handlerCors(HttpServletRequest request,
                                    HttpServletResponse response,
                                    List<String> assignOriginList) {
        if (A.isNotEmpty(assignOriginList) && assignOriginList.contains(request.getHeader(HttpHeaders.ORIGIN))) {
            handlerCors(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        handlerCors((HttpServletRequest) request, (HttpServletResponse) response);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
