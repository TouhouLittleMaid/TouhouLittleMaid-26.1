package com.github.tartaricacid.touhoulittlemaid.client.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为自定义资源包资源提供抽象访问接口。
 * 路径相对于资源包根目录解析（例如：{@code assets/namespace/path}）。
 */
public interface ResourceAccessor {
    /**
     * 打开指定资源路径对应的输入流。
     *
     * @param path 相对于资源包根目录的资源路径
     * @return 独立的输入流
     * @throws IOException 当资源无法打开时抛出
     */
    InputStream open(String path) throws IOException;

    /**
     * 检查资源是否存在。
     *
     * @param path 相对于资源包根目录的资源路径
     * @return 如果资源存在则返回 true
     */
    boolean exists(String path);
}
