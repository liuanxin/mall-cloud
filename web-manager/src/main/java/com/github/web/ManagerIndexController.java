package com.github.web;

import com.github.common.RenderViewResolver;
import com.github.common.json.JsonResult;
import com.github.common.resource.CollectEnumUtil;
import com.github.common.util.SecurityCodeUtil;
import com.github.common.util.U;
import com.github.liuanxin.api.annotation.ApiGroup;
import com.github.liuanxin.api.annotation.ApiIgnore;
import com.github.liuanxin.api.annotation.ApiMethod;
import com.github.util.ManagerDataCollectUtil;
import com.github.util.ManagerSessionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ApiIgnore
@Controller
public class ManagerIndexController {

    @ResponseBody
    @GetMapping("/")
    public String index() {
        return "api";
    }

    @GetMapping("/change-version")
    public JsonResult version() {
        return JsonResult.success("版本号更改为: " + RenderViewResolver.changeVersion());
    }

    @ApiGroup("common-公共模块")
    @ApiMethod(title = "获取枚举信息", develop = "no body")
    @ApiIgnore(false)
    @GetMapping("/enum")
    @ResponseBody
    public JsonResult enumList(String type) {
        return U.isBlank(type) ?
                JsonResult.success("枚举列表", CollectEnumUtil.enumList(ManagerDataCollectUtil.ENUMS)) :
                JsonResult.success("枚举信息", CollectEnumUtil.enumInfo(type, ManagerDataCollectUtil.ENUMS));
    }

    @GetMapping("/code")
    public void code(HttpServletResponse response, String width, String height,
                     String count, String style) throws IOException {
        SecurityCodeUtil.Code code = SecurityCodeUtil.generateCode(count, style, width, height);

        // 往 session 里面丢值
        ManagerSessionUtil.putImageCode(code.getContent());

        // 向页面渲染图像
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        javax.imageio.ImageIO.write(code.getImage(), "jpeg", response.getOutputStream());
    }
}