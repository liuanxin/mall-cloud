package com.github.util;

import com.github.common.exception.NotLoginException;
import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import com.github.common.util.U;

/** !!! 操作 session 都基于此, 其他地方不允许操作! 避免 session 被滥用 !!! */
public class BackendSessionUtil {

    /** 放在 session 里的图片验证码 key */
    private static final String CODE = BackendSessionUtil.class.getName() + "-CODE";
    /** 放在 session 里的用户 的 key */
    private static final String USER = BackendSessionUtil.class.getName() + "-USER";

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
    public static void whenLogin(User account) {
        BackendSessionUtil sessionModel = BackendSessionUtil.assemblyData(account) ;
        if (U.isNotBlank(sessionModel)) {
            if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                LogUtil.ROOT_LOG.debug("put ({}) in session({})",
                        JsonUtil.toJson(sessionModel), RequestUtils.getSession().getId());
            }
            RequestUtils.getSession().setAttribute(USER, sessionModel);
        }
    }
    */


    /** 获取用户信息 */
    private static BackendSessionModel getSessionInfo() {
        return (BackendSessionModel) RequestUtils.getSession().getAttribute(USER);
    }

    /** 获取用户信息. 没有则使用默认信息 */
    private static BackendSessionModel getSessionInfoWithDefault() {
        BackendSessionModel sessionModel = getSessionInfo();
        return sessionModel == null ? BackendSessionModel.defaultUser() : sessionModel;
    }

    /** 从 session 中获取用户 id */
    public static Long getUserId() {
        return getSessionInfoWithDefault().getId();
    }

    /** 从 session 中获取用户名 */
    public static String getUserName() {
        return getSessionInfoWithDefault().getName();
    }

    /** 验证用户是否有登录, 如果有则返回 true */
    private static boolean hasLogin() {
        return getSessionInfoWithDefault().wasLogin();
    }
    /** 验证登录, 未登录则抛出异常 */
    public static void checkLogin() {
        if (!hasLogin()) {
            throw new NotLoginException();
        }
    }

    /** 退出登录时调用. 清空 session */
    public static void signOut() {
        RequestUtils.getSession().invalidate();
    }
}
