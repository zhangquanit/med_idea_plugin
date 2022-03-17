package com.medlinker.idea.plugin.listener;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.medlinker.idea.plugin.MedConfig;
import com.medlinker.idea.plugin.util.LogUtil;
import com.medlinker.idea.plugin.util.MedUtil;
import com.medlinker.idea.plugin.util.MyNotifier;
import org.jetbrains.annotations.NotNull;


/**
 * @autho zhangquan
 */
public class FileChangeListener implements PsiTreeChangeListener {
    private Project mProject;
    private String mLocalDetectFilePath;
    private String mRemoteDetectFilePath;

    public FileChangeListener(Project project) {
        mProject = project;
        mLocalDetectFilePath = MedConfig.getLocalConfigLibFilePath(project);
        mRemoteDetectFilePath = MedConfig.getRemoteConfigLibFilePath(project);
    }

    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("beforeChildAddition " + psiTreeChangeEvent.getFile());
    }

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("beforeChildRemoval " + psiTreeChangeEvent.getFile());
    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("beforeChildReplacement " + psiTreeChangeEvent.getFile());

    }

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("beforeChildMovement " + psiTreeChangeEvent.getFile());

    }

    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("beforeChildrenChange " + psiTreeChangeEvent.getFile());

    }

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("beforePropertyChange " + psiTreeChangeEvent.getFile());

    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("childAdded " + psiTreeChangeEvent.getFile());

    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("childRemoved " + psiTreeChangeEvent.getFile());

    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("childReplaced " + psiTreeChangeEvent.getFile());

    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        PsiFile psiFile = psiTreeChangeEvent.getFile();
//        System.out.println("childrenChanged " + psiFile);

        if (null != psiFile && null != psiFile.getVirtualFile()) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile.getPath().equals(mLocalDetectFilePath)) {
                boolean showDiff = MedUtil.checkShowDiff(mProject);
                if (showDiff) { //与服务器版本内容不一致
                    MyNotifier.notify(mProject, MedConfig.TIP_SYNC);
                }
            } else if (virtualFile.getPath().equals(mRemoteDetectFilePath)) {
                boolean fileOpen = FileEditorManager.getInstance(mProject).isFileOpen(virtualFile);
                LogUtil.d("fileOpen=" + fileOpen);
                if (fileOpen) {
                    MyNotifier.notify(mProject, MedConfig.TIP_COMMIT);
                } else {
                    FileEditorManager.getInstance(mProject).openFile(virtualFile, true);
                }
            }
        }
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("childMoved " + psiTreeChangeEvent.getFile());

    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
//        System.out.println("propertyChanged " + psiTreeChangeEvent.getFile());

    }
}
