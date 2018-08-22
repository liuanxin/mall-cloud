package com.github.search.util;

import com.github.common.util.LogUtil;
import com.github.common.util.U;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class FileUtil {

    private static final String TMP = System.getProperty("java.io.tmpdir");

    private static String getFileName(String index, String type) {
        return U.addSuffix(TMP) + index + type.replace("_", "-");
    }

    public static String readFile(String index, String type) {
        String file = getFileName(index, type);
        try {
            return Files.asCharSource(new File(file), StandardCharsets.UTF_8).read();
        } catch (FileNotFoundException e) {
            if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                LogUtil.ROOT_LOG.debug("no file ({})", file);
            }
        } catch (IOException e) {
            if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                LogUtil.ROOT_LOG.info(String.format("read from file(%s) exception", file), e);
            }
        }
        return U.EMPTY;
    }

    public static void writeFile(String index, String type, String content) {
        String file = getFileName(index, type);
        try {
            Files.asCharSink(new File(file), StandardCharsets.UTF_8).write(content);
        } catch (FileNotFoundException e) {
            if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                LogUtil.ROOT_LOG.debug("no file ({})", file);
            }
        } catch (IOException e) {
            if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                LogUtil.ROOT_LOG.info(String.format("read from file(%s) exception", file), e);
            }
        }
    }

    public static void delete(String index, String type) {
        String fileName = getFileName(index, type);
        boolean flag = new File(fileName).delete();
        if (LogUtil.ROOT_LOG.isInfoEnabled()) {
            LogUtil.ROOT_LOG.info("delete file({}) {}", fileName, flag);
        }
    }
}
