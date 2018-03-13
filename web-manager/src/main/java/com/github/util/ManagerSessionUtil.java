package com.github.util;

import com.github.common.exception.ForbiddenException;
import com.github.common.exception.NotLoginException;
import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import com.github.common.util.U;

/** !!! 操作 session 都基于此, 其他地方不允许操作! 避免 session 被滥用 !!! */
public class ManagerSessionUtil {

    /** 放在 session 里的图片验证码 key */
    private static final String CODE = ManagerSessionUtil.class.getName() + "-CODE";
    /** 放在 session 里的用户 的 key */
    private static final String USER = ManagerSessionUtil.class.getName() + "-USER";

    /** 验证图片验证码 */
    public static boolean checkImageCode(String code) {
        if (U.isBlank(code)) {
            return false;
        }

        Object securityCode = RequestUtils.getSession().getAttribute(CODE);
        return securityCode != null && code.equalsIgnoreCase(securityCode.toString());
    }
    /** 将图片验证码的值放入 session */
    public static void putImageCode(String code) {
        RequestUtils.getSession().setAttribute(CODE, code);
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("put image code({}) in session({})", code, RequestUtils.getSession().getId());
        }
    }

    // /** 登录之后调用此方法, 主要就是将 用户信息、可访问的 url 等放入 session */
    /*
    public static void whenLogin(User account, List<Permission> permissionList) {
        ManagerSessionModel sessionModel = ManagerSessionModel.assemblyData(
                getSessionInfo(), RequestUtils.getDomain(), account,permissionList) ;
        if (U.isNotBlank(sessionModel)) {
            if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                LogUtil.ROOT_LOG.debug("put ({}) in session({})", sessionModel, RequestUtils.getSession().getId());
            }
        }
        RequestUtils.getSession().setAttribute(USER, sessionModel);
    }
    */


    /** 获取用户信息 */
    private static ManagerSessionModel getSessionInfo() {
        return (ManagerSessionModel) RequestUtils.getSession().getAttribute(USER);
    }

    /** 获取用户信息. 没有则使用默认信息 */
    private static ManagerSessionModel getSessionInfoWithDefault() {
        ManagerSessionModel sessionModel = getSessionInfo();
        return sessionModel == null ? ManagerSessionModel.defaultUser() : sessionModel;
    }

    /** 从 session 中获取用户 id */
    public static Long getUserId() {
        return getSessionInfoWithDefault().getId();
    }

    /** 从 session 中获取用户名 */
    public static String getUserName() {
        return getSessionInfoWithDefault().getName();
    }

    /** 是否是超级管理员, 是则返回 true */
    public static boolean isSuper() {
        return getSessionInfoWithDefault().wasSuper();
    }

    /** 验证用户是否有登录, 如果有则返回 true */
    public static boolean hasLogin() {
        return getSessionInfoWithDefault().wasLogin();
    }
    /** 验证登录, 未登录则抛出异常 */
    public static void checkLogin() {
        if (!hasLogin()) {
            throw new NotLoginException();
        }
    }

    /** 是否有访问权限, 有则返回 true */
    public static boolean hasPermission() {
        // 没有登录当然也就表示没有权限了
        checkLogin();
        // 管理员直接放过权限检查
        if (isSuper()) {
            return true;
        }

        String url = RequestUtils.getRequest().getRequestURI();
        String method = RequestUtils.getRequest().getMethod();
        return getSessionInfo().hasPermission(url, method);
    }

    /** 检查权限, 无权限则抛出异常 */
    public static void checkPermission() {
        if (!hasPermission()) {
            throw new ForbiddenException("您没有(" + RequestUtils.getRequest().getRequestURL().toString() + ")的访问权限");
        }
    }

    /** 退出登录时调用. 清空 session */
    public static void signOut() {
        RequestUtils.getSession().invalidate();
    }
}
