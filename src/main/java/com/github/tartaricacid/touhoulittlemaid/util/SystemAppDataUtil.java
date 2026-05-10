package com.github.tartaricacid.touhoulittlemaid.util;

import com.sun.jna.Platform;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SystemAppDataUtil {
    /**
     * 获取当前系统的 AppData 目录
     *
     * @param appName 应用程序名称，用于创建特定应用的目录
     * @return AppData 目录的 Path 对象
     */
    public static Path getAppDataDir(String appName) {
        if (StringUtils.isBlank(appName)) {
            throw new IllegalArgumentException("Application name cannot be null or empty");
        }

        Path userHome = Paths.get(System.getProperty("user.home"));

        // 使用 JNA Platform 进行平台检测
        if (Platform.isAndroid()) {
            String androidData = System.getenv("EXTERNAL_STORAGE");
            if (androidData != null && !androidData.trim().isEmpty()) {
                return Paths.get(androidData).resolve("Android").resolve("data").resolve(appName);
            }
            // 如果获取不到外部存储路径，返回一个备选路径
            return userHome.resolve("Android").resolve("data").resolve(appName);
        }

        if (Platform.isWindows()) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.trim().isEmpty()) {
                return Paths.get(appData).resolve(appName);
            }
            return userHome.resolve("AppData").resolve("Roaming").resolve(appName);
        }

        if (Platform.isMac()) {
            return userHome.resolve("Library").resolve("Application Support").resolve(appName);
        }

        // Linux 和其他 Unix-like 系统
        // Platform.isLinux() 也可以用，但这里我们作为默认情况处理
        // 因为即使是其他类 Unix 系统，这个路径约定也是通用的
        String xdgData = System.getenv("XDG_DATA_HOME");
        if (xdgData != null && !xdgData.trim().isEmpty()) {
            return Paths.get(xdgData).resolve(appName);
        }
        return userHome.resolve(".local").resolve("share").resolve(appName);
    }

    /**
     * 获取并创建 AppData 目录
     *
     * @param appName 应用程序名称
     * @return AppData 目录的 Path 对象
     * @throws IOException 如果目录创建失败
     */
    public static Path getAndCreateAppDataDir(String appName) throws IOException {
        Path dir = getAppDataDir(appName);
        if (!dir.toFile().isDirectory()) {
            Files.createDirectories(dir);
        }
        return dir;
    }
}
