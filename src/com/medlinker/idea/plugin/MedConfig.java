package com.medlinker.idea.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.medlinker.idea.plugin.util.LogUtil;
import com.medlinker.idea.plugin.util.MedUtil;

import java.io.File;

/**
 * @autho zhangquan
 */
public final class MedConfig {
    public static final String PLUGIN_ID = "com.medlinker.idea.plugin.android";
    public static final String PLUGIN_JAR = "med_idea_plugin.jar";
    public static final String CONFIG_LIB_FILE = "config_libs.gradle";
    public static final String CONFIG_LIB_GIT = "https://git.medlinker.com/android/x_libs.git";

    public static final String REMOTE_CONFIG_NOT_EXIST = "未检测到远程仓库文件：x_dev/libs/config_libs.gradle";
    public static final String SOURCE_CONFIG_NOT_EXIST = "未检测到配置文件：x_dev/test/config.gradle";
    public static final String CONFIG_LIB_GIT_COMMIT = "更新组件库";
    public static final String TIP_SYNC = "更新了依赖库，别忘了同步到x_dev/libs/config_libs.gradle";
    public static final String TIP_COMMIT = "更新了依赖库，别忘了提交到远程仓库哦！！";
    public static final String TIP_PUSH = "更新了依赖库，是否提交到远程仓库？";
    public static final String TIP_DIFF = "检测到本地依赖库与远程仓库文件不一致，查看修改？";

    public static final String PLUGIN_TITLE = "医联插件";
    public static final String PLUGIN_UPDATE_SUCCESS = "插件更新成功，重启后即可生效，是否重启?";
    public static final String PLUGIN_UPDATE_FAIL = "插件自动更新失败，请手动更新";
    public static final String PLUGIN_FETCH_FAIL = "解析插件包失败(x_dev/libscript/" + MedConfig.PLUGIN_JAR + ")";
    public static final String PLUGIN_NEW_VERSION = "发现新版插件 V%s，是否更新？";
    public static final String PLUGIN_NEWEST = "当前已是最新版本(v%s)";
    public static final String PLUGIN_NOT_EXIST = "插件包(x_dev/libscript/" + MedConfig.PLUGIN_JAR + ")不存在";


    private static String mLocalConfigLibFilePath; //项目本地依赖库config_libs.gradle路径
    private static VirtualFile mLocalConfigLibFile; //项目本地依赖库config_libs.gradle文件
    private static String mRemoteConfigLibDir; //远程依赖库目录
    private static String mRecomteConfigLibFilePath;//远程依赖库config_libs.gradle路径
    private static VirtualFile mRemoteConfigLibFile; //远程依赖库config_libs.gradle文件
    private static String mPluginJarPath; //插件包路径
    private static String mSourceConfigFilePath; //源码依赖配置文件路径
    private static VirtualFile mSourceConfigFile; //源码依赖配置文件


    public static String getPluginJarPath(Project project) {
        if (null == mPluginJarPath || !mPluginJarPath.contains(project.getBasePath())) {
            mPluginJarPath = new StringBuffer()
                    .append(project.getBasePath()).append(File.separator)
                    .append("x_dev").append(File.separator)
                    .append("libscript").append(File.separator)
                    .append(MedConfig.PLUGIN_JAR)
                    .toString();
        }
        return mPluginJarPath;
    }


    public static String getRemoteConfigLibDir(Project project) {
        if (null == mRemoteConfigLibDir || !mRemoteConfigLibDir.contains(project.getBasePath())) {
            mRemoteConfigLibDir = new StringBuffer()
                    .append(project.getBasePath())
                    .append(File.separator)
                    .append("x_dev")
                    .append(File.separator)
                    .append("libs")
                    .toString();
        }
        return mRemoteConfigLibDir;
    }

    public static String getRemoteConfigLibFilePath(Project project) {
        if (null == mRecomteConfigLibFilePath || !mRecomteConfigLibFilePath.contains(project.getBasePath())) {
            mRecomteConfigLibFilePath = new StringBuffer()
                    .append(getRemoteConfigLibDir(project))
                    .append(File.separator)
                    .append(MedConfig.CONFIG_LIB_FILE)
                    .toString();
        }
        return mRecomteConfigLibFilePath;
    }

    public static String getLocalConfigLibFilePath(Project project) {
        if (null == mLocalConfigLibFilePath || !mLocalConfigLibFilePath.contains(project.getBasePath())) {
            mLocalConfigLibFilePath = new StringBuffer()
                    .append(project.getBasePath())
                    .append(File.separator)
                    .append(MedConfig.CONFIG_LIB_FILE)
                    .toString();
        }
        return mLocalConfigLibFilePath;
    }

    public static VirtualFile getRemoteConfigLibFile(Project project) {
        mRemoteConfigLibFile = MedUtil.findTargetFile(project, MedConfig.CONFIG_LIB_FILE, getRemoteConfigLibFilePath(project), mRemoteConfigLibFile);
        return mRemoteConfigLibFile;
    }

    public static VirtualFile getLocalConfigLibFile(Project project) {
        mLocalConfigLibFile = MedUtil.findTargetFile(project, MedConfig.CONFIG_LIB_FILE, getLocalConfigLibFilePath(project), mLocalConfigLibFile);
        return mLocalConfigLibFile;
    }

    public static String getSourceConfigFilePath(Project project) {
        if (null == mSourceConfigFilePath || !mSourceConfigFilePath.contains(project.getBasePath())) {
            mSourceConfigFilePath = new StringBuffer()
                    .append(project.getBasePath())
                    .append(File.separator)
                    .append("x_dev")
                    .append(File.separator)
                    .append("test")
                    .append(File.separator)
                    .append("config.gradle")
                    .toString();
        }

        return mSourceConfigFilePath;
    }

    public static VirtualFile getSourceConfigFile(Project project) {
        mSourceConfigFile = MedUtil.findTargetFile(project, "config.gradle", getSourceConfigFilePath(project), mSourceConfigFile);
        return mSourceConfigFile;
    }


    public static void clearCache() {
        LogUtil.d("---清除缓存");

        mLocalConfigLibFilePath = null;
        mLocalConfigLibFile = null;
        mRecomteConfigLibFilePath = null;
        mRemoteConfigLibFile = null;
        mRemoteConfigLibDir = null;
        mPluginJarPath = null;
        mSourceConfigFilePath = null;
        mSourceConfigFile = null;
    }

}
