package com.medlinker.idea.plugin.util;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.ide.plugins.PluginInstaller;
import com.intellij.ide.plugins.PluginStateListener;
import com.intellij.ide.plugins.newui.MyPluginModel;
import com.intellij.ide.ui.search.SearchUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.medlinker.idea.plugin.MedConfig;
import icons.MedPluginIcons;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * @autho zhangquan
 */
public class IdeaSettings {

    /**
     * 打开Preference面板
     */
    public static void openSetting(Project project) {
        ConfigurableGroup[] configurableGroups = ShowSettingsUtilImpl.getConfigurableGroups(project, true);
        ShowSettingsUtil.getInstance().showSettingsDialog(project, configurableGroups);
    }

    /**
     * 打开Preference面板 并选中Plugin
     *
     * @param project
     */
    public static void openPluginSetting(Project project) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, "Plugins");
    }

    /**
     * 安装插件
     *
     * @param project
     */
    public static void installPlugin(Project project) {
        String pluginJarPath = MedConfig.getPluginJarPath(project);
        File file = new File(pluginJarPath);
//        installFromDisk(file);

        Path path = FileSystems.getDefault().getPath(pluginJarPath);
        IdeaPluginDescriptorImpl pluginDescriptor = (IdeaPluginDescriptorImpl) VersionUpdater.getPluginDescriptor(path);
        PluginInstaller.addStateListener(new PluginStateListener() {
            @Override
            public void install(@NotNull IdeaPluginDescriptor ideaPluginDescriptor) {
                String idString = ideaPluginDescriptor.getPluginId().getIdString();
                LogUtil.d("安装插件 install  id=" + idString);
                if (MedConfig.PLUGIN_ID.equals(idString)) {
                    int choice = Messages.showOkCancelDialog(MedConfig.PLUGIN_UPDATE_SUCCESS, MedConfig.PLUGIN_TITLE, "确定", "取消", MedPluginIcons.ICON);
                    if (choice == 0) {
                        ApplicationManager.getApplication().exit(true, false, true);
                    }
                }
            }

            @Override
            public void uninstall(@NotNull IdeaPluginDescriptor ideaPluginDescriptor) {

            }
        });

        boolean methodError = false;
        try {
            //兼容AndroidStudio
            Method installWithoutRestart = PluginInstaller.class.getDeclaredMethod("installWithoutRestart", Path.class, IdeaPluginDescriptorImpl.class, Component.class);
            installWithoutRestart.setAccessible(true);
            installWithoutRestart.invoke(null, path, pluginDescriptor, null);
            LogUtil.d("installWithoutRestart 反射执行成功");
        } catch (Exception e) {
            methodError = true;
            LogUtil.d("installWithoutRestart 反射执行失败 e=" + e.getMessage());
        }

        // idea生效 AndroidStudio不生效
        if (methodError) {
            try {
                PluginInstaller.installWithoutRestart(file, pluginDescriptor, null);
                methodError = false;
            } catch (Exception e) {
            }
        }

        if (methodError) {
            int choice = Messages.showOkCancelDialog(MedConfig.PLUGIN_UPDATE_FAIL, MedConfig.PLUGIN_TITLE, "确定", "取消", MedPluginIcons.ICON);
            if (choice == 0) {
                IdeaSettings.openPluginSetting(project);
            }
        }
//        MockUtil.refelectMethod(project,PluginInstaller.class,"install");
    }

    /**
     * 安装插件包
     * 备注：AndroidStudio4.0上不生效
     */
    private static void installFromDisk(File file) {
        boolean result = PluginInstaller.installFromDisk(new MyPluginModel(), file, pluginInstallCallbackData -> {

        }, null);

        //安装成功  弹框提示重启
        if (result) {
//            PluginManagerConfigurable.showRestartDialog(); //系统重启弹框
            int choice = Messages.showOkCancelDialog(MedConfig.PLUGIN_UPDATE_SUCCESS, MedConfig.PLUGIN_TITLE, "确定", "取消", MedPluginIcons.ICON);
            if (choice == 0) {
                ApplicationManager.getApplication().exit(true, false, true);
            }
        }
    }

    /**
     * 根据名字查找设置
     */
    public static Configurable getConfigurableByName(Project project, String name) {
        ConfigurableGroup[] configurableGroups = ShowSettingsUtilImpl.getConfigurableGroups(project, true);

        for (ConfigurableGroup group : configurableGroups) {
            Iterator iterator = SearchUtil.expandGroup(group).iterator();
            while (iterator.hasNext()) {
                Configurable configurable = (Configurable) iterator.next();
                if (configurable.getDisplayName().equals(name)) {
                    return configurable;
                }
            }
        }
        return null;
    }
}
