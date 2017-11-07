package com.github.common.resource;

import com.google.common.collect.Lists;
import com.github.common.util.LogUtil;
import com.github.common.util.U;
import com.github.common.util.A;
import org.apache.ibatis.type.TypeHandler;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class LoaderHandler {

    /**
     * 基于指定的类(会基于此类来获取类加载器), 在指定的包名下获取 mybatis 的类型处理器
     */
    public static TypeHandler[] getHandleArray(Class clazz, String classPackage) {
        if (LogUtil.ROOT_LOG.isTraceEnabled()) {
            LogUtil.ROOT_LOG.trace("{} in ({})", clazz, clazz.getProtectionDomain().getCodeSource().getLocation());
        }
        List<TypeHandler> handlerList = Lists.newArrayList();
        String packageName = classPackage.replace(".", "/");
        URL url = clazz.getClassLoader().getResource(packageName);
        if (url != null) {
            if ("file".equals(url.getProtocol())) {
                File parent = new File(url.getPath());
                if (parent.isDirectory()) {
                    File[] files = parent.listFiles();
                    if (A.isNotEmpty(files)) {
                        for (File file : files) {
                            TypeHandler handler = getTypeHandler(file.getName(), classPackage);
                            if (handler != null) {
                                handlerList.add(handler);
                            }
                        }
                    }
                }
            } else if ("jar".equals(url.getProtocol())) {
                try (JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile()) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.startsWith(packageName) && name.endsWith(".class")) {
                            TypeHandler handler = getTypeHandler(name.substring(name.lastIndexOf("/") + 1), classPackage);
                            if (handler != null) {
                                handlerList.add(handler);
                            }
                        }
                    }
                } catch (IOException e) {
                    if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                        LogUtil.ROOT_LOG.error("can't load jar file: " + e.getMessage());
                    }
                }
            }
        }
        return handlerList.toArray(new TypeHandler[handlerList.size()]);
    }

    private static TypeHandler getTypeHandler(String name, String classPackage) {
        if (U.isNotBlank(name)) {
            String className = classPackage + "." + name.replace(".class", "");
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz != null && TypeHandler.class.isAssignableFrom(clazz)) {
                    return (TypeHandler) clazz.newInstance();
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                    LogUtil.ROOT_LOG.error(String.format("TypeHandler clazz (%s) exception: ", className) + e.getMessage());
                }
            }
        }
        return null;
    }
}
