package com.github.common.export;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExportColumn {

    /** 标注在类字段上, 表示这个字段导出时的列名(如果想定义数据格式, 用英文下划线(|)隔开) */
    String value();
}
