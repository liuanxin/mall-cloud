package com.github.vo;

import com.github.global.enums.Gender;
import com.github.liuanxin.api.annotation.ApiParam;
import com.github.liuanxin.api.annotation.ApiReturn;
import lombok.Data;

@Data
public class DemoVo {
    @ApiReturn(desc = "用户 id")
    private String userId;
    @ApiParam(dataType = "int", desc = "性别")
    private Gender gender;
}
