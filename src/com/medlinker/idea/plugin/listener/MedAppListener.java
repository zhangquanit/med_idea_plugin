package com.medlinker.idea.plugin.listener;

import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.project.impl.ProjectLifecycleListener;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiManager;
import com.medlinker.idea.plugin.MedConfig;
import com.medlinker.idea.plugin.util.CmdUtil;
import com.medlinker.idea.plugin.util.LogUtil;
import com.medlinker.idea.plugin.util.MedUtil;
import com.medlinker.idea.plugin.util.VersionUpdater;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @autho zhangquan
 */
public class MedAppListener implements ProjectManagerListener, ProjectLifecycleListener {
    public static Project mProject;

    @Override
    public void projectOpened(@NotNull Project project) {
        mProject = project;
        LogUtil.d("MedAppListener projectOpened path=" + project.getBasePath());
        MedConfig.clearCache();
        //监听文件编辑
        PsiManager.getInstance(project).addPsiTreeChangeListener(new FileChangeListener(project));
//        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new MyBulkFileListener(project));
        //监听文件打开或关闭
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new MedFileEditorListener(project));


        StartupManager.getInstance(project).runWhenProjectIsInitialized(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("project is initialized");
                //版本检测
                VersionUpdater.checkUpdate(project);
                //依赖库检测
                CmdUtil.execRemoteConfigShellCmd(project, CmdUtil.CMD_PULL);
                if (MedUtil.checkShowDiff(mProject)) {
                    int result = Messages.showOkCancelDialog(MedConfig.TIP_DIFF, "依赖库文件", "确定", "取消", null);
                    if (result == 0) {
                        MedUtil.showDiffWithConfigLibFile(mProject);
                    }
                }
            }
        });
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        mProject = null;
        MedConfig.clearCache();
    }

}
