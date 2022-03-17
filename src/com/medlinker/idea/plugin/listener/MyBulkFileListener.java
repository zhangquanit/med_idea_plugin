package com.medlinker.idea.plugin.listener;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.medlinker.idea.plugin.MedConfig;
import com.medlinker.idea.plugin.util.MedUtil;
import com.medlinker.idea.plugin.util.MyNotifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @autho zhangquan
 */
public class MyBulkFileListener implements BulkFileListener {
    private Project mProject;
    private String mLocalDetectFilePath;
    private String mRemoteDetectFilePath;

    public MyBulkFileListener(Project project) {
        mProject = project;
        mLocalDetectFilePath = MedConfig.getLocalConfigLibFilePath(project);
        mRemoteDetectFilePath = MedConfig.getRemoteConfigLibFilePath(project);
    }

    @Override
    public void before(@NotNull List<? extends VFileEvent> events) {

    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            if (event.getPath().equals(mLocalDetectFilePath)) {
                MedUtil.saveAll();
                boolean showDiff = MedUtil.checkShowDiff(mProject);
                if (showDiff) { //与服务器版本内容不一致
                    MyNotifier.notify(mProject, MedConfig.TIP_SYNC);
                }
            }else if(event.getPath().equals(mRemoteDetectFilePath)){
                MedUtil.saveAll();
                VirtualFile file = event.getFile();
                boolean fileOpen = FileEditorManager.getInstance(mProject).isFileOpen(file);
                if (fileOpen) {
                    MyNotifier.notify(mProject, MedConfig.TIP_COMMIT);
                } else {
                    FileEditorManager.getInstance(mProject).openFile(file, true);
                }
            }
        }
    }


}
