package com.github.common;

import com.github.common.util.GenerateEnumHandler;
import org.junit.Test;

public class CommonsGenerateEnumHandler {

    @Test
    public void generate() {
        GenerateEnumHandler.generateEnum(getClass(), Const.BASE_PACKAGE, "common");
    }
}
