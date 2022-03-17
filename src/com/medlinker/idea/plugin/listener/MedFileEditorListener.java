package com.medlinker.idea.plugin.listener;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.medlinker.idea.plugin.MedConfig;
import com.medlinker.idea.plugin.util.CmdUtil;
import com.medlinker.idea.plugin.util.MedUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @autho zhangquan
 */
public class MedFileEditorListener implements FileEditorManagerListener {
    private Project mProject;
    private String mLocalConfigLibFilePath;
    private String mRemoteConfigLibFilePath;

    public MedFileEditorListener(Project project) {
        mProject = project;
        mLocalConfigLibFilePath = MedConfig.getLocalConfigLibFilePath(mProject);
        mRemoteConfigLibFilePath = MedConfig.getRemoteConfigLibFilePath(mProject);
    }

    @Override
    public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull Pair<FileEditor[], FileEditorProvider[]> editors) {

    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (!mProject.isInitialized()) return;
        if (mLocalConfigLibFilePath.equals(file.getPath())) {
            if (MedUtil.checkShowDiff(mProject)) {
                int result = Messages.showOkCancelDialog(MedConfig.TIP_DIFF, "依赖库文件", "确定", "取消", null);
                if (result == 0) {
                    MedUtil.showDiffWithConfigLibFile(mProject);
                }
            }
        }
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (mLocalConfigLibFilePath.equals(file.getPath())) {
            MedUtil.saveAll();
            if (MedUtil.checkShowDiff(mProject)) {
                int result = Messages.showOkCancelDialog(MedConfig.TIP_DIFF, "依赖库文件", "确定", "取消", null);
                if (result == 0) { //确定
                    MedUtil.showDiffWithConfigLibFile(mProject);
                }
            }
        } else if (mRemoteConfigLibFilePath.equals(file.getPath())) { //关闭
            MedUtil.saveAll();
            String result = CmdUtil.execRemoteConfigShellCmd(mProject, CmdUtil.CMD_DIFF);
            if (!MedUtil.isEmpty(result) && result.contains("diff --git a/config_libs.gradle b/config_libs.gradle")) {
                int choice = Messages.showOkCancelDialog(MedConfig.TIP_PUSH, "依赖库文件", "确定", "取消", null);
                if (choice == 0) { //确定
                    CmdUtil.execRemoteConfigShellCmd(mProject, CmdUtil.CMD_PUSH);
                }
            }
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {

    }
}
