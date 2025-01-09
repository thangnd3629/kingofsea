package com.supergroup.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilTest {

    @Test
    void getListStringFromRawStringComma() {
        var res = StringUtil.getListStringFromRawStringComma("idev,vedi,comma,java");
        Assertions.assertEquals(4, res.size());
    }
}