package com.github.common.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.common.exception.NotLoginException;
import com.github.liuanxin.api.annotation.ApiReturn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** <span style="color:red;">!!!此实体类请只在 Controller 中使用, 且只调用其 static 方法!!!</span> */
@Setter
@Getter
@NoArgsConstructor
public class JsonResult<T> {

    /**
     * 返回码. 前台根据此值控制页面扭转.
     *
     * @see JsonCode
     */
    @ApiReturn(desc = "返回码. 根据此值控制页面扭转: 0.显示 msg, 1.业务处理, 10.导向登录页")
    private int code;

    /** 返回说明. 如: 用户名密码错误, 收货地址添加成功 等 */
    @ApiReturn(desc = "返回说明. 如: 用户名密码错误, 收货地址添加成功 等")
    private String msg;

    /** 返回的数据. 具体是返回实体 {"id":1} 还是列表 [{"id":1},{"id":2}] 依具体的业务而定 */
    @ApiReturn(desc = "返回的数据. 实体 {\"id\":1} 还是列表 [{\"id\":1},{\"id\":2}] 依具体的业务而定")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    private JsonResult(JsonCode code, String msg) {
        this.code = code.flag;
        this.msg = msg;
    }
    private JsonResult(JsonCode code, String msg, T data) {
        this(code, msg);
        this.data = data;
    }


    // ---------- 在 service 中请只使用下面的静态方法就好了. 不要 new JsonResult()... 这样操作 ----------

    /** 请求成功且不需要返回数据, 当返回 "地址添加成功" 这一类说明时 */
    public static JsonResult success(String msg) {
        return new JsonResult(JsonCode.SUCCESS, msg);
    }
    /** 请求成功且有返回数据时 */
    public static <T> JsonResult<T> success(String msg, T data) {
        return new JsonResult<>(JsonCode.SUCCESS, msg, data);
    }

    /** 请求失败时 */
    public static JsonResult fail(String msg) {
        return new JsonResult(JsonCode.FAIL, msg);
    }

    /** 未登录时 */
    public static JsonResult notLogin() {
        return new JsonResult(JsonCode.NOT_LOGIN, NotLoginException.DEFAULT_MSG);
    }

    /** 无权限时 */
    public static JsonResult notPermission(String msg) {
        return new JsonResult(JsonCode.NOT_PERMISSION, msg);
    }
}
