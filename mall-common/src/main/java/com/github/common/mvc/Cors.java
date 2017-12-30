package com.github.common.mvc;

import com.github.common.Const;
import com.github.common.util.A;
import com.github.common.util.U;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/** 处理跨域. 放在所有处理的最前面!!! */
public final class Cors {

    private static final String ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String METHODS = "Access-Control-Allow-Methods";
    private static final String HEADERS = "Access-Control-Allow-Headers";

    public static void handlerCors(HttpServletRequest request,
                                   HttpServletResponse response) {
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
            if (U.isBlank(HEADERS)) {
                response.addHeader(HEADERS, "*");
            }
        }
    }

    /** 在指定域名内的才加 cors 进 header */
    public static void handlerCors(HttpServletRequest request,
                                   HttpServletResponse response, List<String> assignOriginList) {
        if (A.isNotEmpty(assignOriginList) && assignOriginList.contains(request.getHeader(HttpHeaders.ORIGIN))) {
            handlerCors(request, response);
        }
    }
}
