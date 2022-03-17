package com.medlinker.idea.plugin.util;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginDescriptorLoader;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.medlinker.idea.plugin.MedConfig;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @autho zhangquan
 */
public class VersionUpdater {

    public static VersionUpdateResult checkUpdate(Project project) {
        try {
            IdeaPluginDescriptor curPlugin = PluginManager.getPlugin(PluginId.getId(MedConfig.PLUGIN_ID));
            String curVersion = curPlugin.getVersion();
            LogUtil.d("当前插件版本：" + curVersion);
            VersionUpdateResult.PLUGIN_NOT_NEW.desc = String.format(MedConfig.PLUGIN_NEWEST, curVersion);

            //发布插件
            String jarPath = MedConfig.getPluginJarPath(project);
            if (!new File(jarPath).exists()) {
                return VersionUpdateResult.PLUGIN_NOT_EXIT;
            }
            Path path = FileSystems.getDefault().getPath(jarPath);
            IdeaPluginDescriptor checkPlugin = getPluginDescriptor(path);
            if (null == checkPlugin) {
                return VersionUpdateResult.PLUGIN_PARSE_FAIL;
            }

            String releaseVersion = checkPlugin.getVersion();
            LogUtil.d("最新发布插件版本：" + checkPlugin.getVersion());
            if (curVersion.equals(releaseVersion)) {
                return VersionUpdateResult.PLUGIN_NOT_NEW;
            }
            boolean hasNewVersion = compareVersions(releaseVersion, curVersion);
            if (hasNewVersion) {
//                int result = Messages.showOkCancelDialog(String.format(MedConfig.PLUGIN_NEW_VERSION,releaseVersion), MedConfig.PLUGIN_TITLE, "确定", "取消", MedPluginIcons.ICON);
//                if (result == 0) {
//                        IdeaSettings.openPluginSetting(project);
                IdeaSettings.installPlugin(project); //自动更新
//                }
            }
            return hasNewVersion ? VersionUpdateResult.PLUGIN_NEW : VersionUpdateResult.PLUGIN_NOT_NEW;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return VersionUpdateResult.PLUGIN_NOT_NEW;
    }

    public static IdeaPluginDescriptor getPluginDescriptor(Path path) {
        IdeaPluginDescriptor checkPlugin = null;
        try {
            checkPlugin = PluginDescriptorLoader.loadDescriptorFromArtifact(path, null);
//                FileSystem fileSystem = FileSystems.newFileSystem(path, null);
//                PluginManager.loadDescriptorFromFile(null, path, new SafeJdomFactory.BaseSafeJdomFactory(), DisabledPluginsState.disabledPlugins());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == checkPlugin) {
            try {
                checkPlugin = PluginManager.loadDescriptor(path, "plugin.xml");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return checkPlugin;
    }

    public static boolean compareVersions(String releaseVersion, String curVersion) {
        if (MedUtil.isEmpty(releaseVersion) || MedUtil.isEmpty(curVersion)) {
            return false;
        }
        String[] str1 = releaseVersion.split("\\.");
        String[] str2 = curVersion.split("\\.");

        try {
            if (str1.length == str2.length) {
                for (int i = 0; i < str1.length; i++) {
                    if (Integer.parseInt(str1[i]) > Integer.parseInt(str2[i])) {
                        return true;
                    } else if (Integer.parseInt(str1[i]) < Integer.parseInt(str2[i])) {
                        return false;
                    } else if (Integer.parseInt(str1[i]) == Integer.parseInt(str2[i])) {

                    }
                }
            } else {
                if (str1.length > str2.length) {
                    for (int i = 0; i < str2.length; i++) {
                        if (Integer.parseInt(str1[i]) > Integer.parseInt(str2[i])) {
                            return true;
                        } else if (Integer.parseInt(str1[i]) < Integer.parseInt(str2[i])) {
                            return false;

                        } else if (Integer.parseInt(str1[i]) == Integer.parseInt(str2[i])) {
                            if (str2.length == 1) {
                                continue;
                            }
                            if (i == str2.length - 1) {

                                for (int j = i; j < str1.length; j++) {
                                    if (Integer.parseInt(str1[j]) != 0) {
                                        return true;
                                    }
                                    if (j == str1.length - 1) {
                                        return false;
                                    }

                                }
                                return true;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < str1.length; i++) {
                        if (Integer.parseInt(str1[i]) > Integer.parseInt(str2[i])) {
                            return true;
                        } else if (Integer.parseInt(str1[i]) < Integer.parseInt(str2[i])) {
                            return false;

                        } else if (Integer.parseInt(str1[i]) == Integer.parseInt(str2[i])) {
                            if (str1.length == 1) {
                                continue;
                            }
                            if (i == str1.length - 1) {
                                return false;

                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static enum VersionUpdateResult {
        /**
         * 插件包不存在
         */
        PLUGIN_NOT_EXIT(MedConfig.PLUGIN_NOT_EXIST),
        /**
         * 发现新插件包
         */
        PLUGIN_NEW(""),
        /**
         * 当前插件已是最新版本
         */
        PLUGIN_NOT_NEW(MedConfig.PLUGIN_NEWEST),
        /**
         * 插件包解析失败
         */
        PLUGIN_PARSE_FAIL(MedConfig.PLUGIN_FETCH_FAIL);

        public String desc;

        private VersionUpdateResult(String desc) {
            this.desc = desc;
        }
    }
}

