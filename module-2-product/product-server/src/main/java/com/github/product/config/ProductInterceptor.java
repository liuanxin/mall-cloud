package com.github.product.config;

import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 商品模块的 web 拦截器
 *
 * @author https://github.com/liuanxin
 */
public class ProductInterceptor implements HandlerInterceptor {

    @Value("${online:false}")
    private boolean online;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        LogUtil.bind(RequestUtils.logContextInfo(online));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        if (ex != null) {
            if (LogUtil.ROOT_LOG.isDebugEnabled())
                LogUtil.ROOT_LOG.debug("request was over, but have exception: " + ex.getMessage());
        }
        LogUtil.unbind();
    }
}
