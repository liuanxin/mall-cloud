package com.github.common.json;

import com.fasterxml.jackson.annotation.JsonInclude;
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
     * 返回码
     *
     * @see JsonCode
     */
    @ApiReturn(desc = "返回码")
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

    /** 请求失败 */
    public static <T> JsonResult<T> fail(String msg) {
        return new JsonResult<T>(JsonCode.FAIL, msg);
    }

    /** 参数错误 */
    public static <T> JsonResult<T> badRequest(String msg) {
        return new JsonResult<T>(JsonCode.BAD_REQUEST, msg);
    }

    /** 未登录 */
    public static <T> JsonResult<T> notLogin(String msg) {
        return new JsonResult<T>(JsonCode.NOT_LOGIN, msg);
    }

    /** 无权限 */
    public static <T> JsonResult<T> notPermission(String msg) {
        return new JsonResult<T>(JsonCode.NOT_PERMISSION, msg);
    }

    /** 未找到 */
    public static <T> JsonResult<T> notFound() {
        return new JsonResult<>(JsonCode.NOT_FOUND, "404");
    }

    /** 请求成功且不需要返回数据, 当返回 "地址添加成功" 这一类说明时 */
    public static <T> JsonResult<T> success(String msg) {
        return new JsonResult<T>(JsonCode.SUCCESS, msg);
    }
    /** 请求成功且有返回数据时 */
    public static <T> JsonResult<T> success(String msg, T data) {
        return new JsonResult<T>(JsonCode.SUCCESS, msg, data);
    }
}
