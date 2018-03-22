package com.github.dto;

import com.github.global.enums.Gender;
import com.github.liuanxin.api.annotation.ApiParam;
import lombok.Data;

@Data
public class DemoDto {
    @ApiParam(must = true, desc = "用户 id")
    private Long userId;
    @ApiParam(dataType = "int", desc = "性别")
    private Gender gender;
}
