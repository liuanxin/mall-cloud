package com.github.common.resource;

import com.github.common.util.LogUtil;
import com.google.common.collect.Lists;
import com.github.common.util.A;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class LoaderResource {

    /**
     * 从指定的目录下获取 mybatis 要加载的 xml 文件
     */
    public static Resource[] getResourceArray(Class clazz, String[] resourcePath) {
        if (LogUtil.ROOT_LOG.isTraceEnabled()) {
            LogUtil.ROOT_LOG.trace("{} in ({})", clazz, clazz.getProtectionDomain().getCodeSource().getLocation());
        }
        List<Resource> resourceList = Lists.newArrayList();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(clazz.getClassLoader());
        for (String path : resourcePath) {
            try {
                Resource[] resources = resolver.getResources(path);
                if (A.isNotEmpty(resources)) {
                    Collections.addAll(resourceList, resources);
                }
            } catch (IOException e) {
                if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                    LogUtil.ROOT_LOG.error(String.format("load file(%s) exception: ", path) + e.getMessage());
                }
            }
        }
        return resourceList.toArray(new Resource[resourceList.size()]);
    }
}
