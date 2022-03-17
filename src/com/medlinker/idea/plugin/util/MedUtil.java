package com.medlinker.idea.plugin.util;

import com.intellij.diagnostic.LoadingState;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.ModalityHelper;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.mac.foundation.MacUtil;
import com.medlinker.idea.plugin.MedConfig;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @autho zhangquan
 */
public class MedUtil {


    public static String getFileContent(VirtualFile file) {
        String content = "";
        try {
            content = new String(file.contentsToByteArray(), file.getCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void openFileInEditor(Project project, VirtualFile virtualFile) {
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }

    public static void showDiff(Project project, VirtualFile file1, VirtualFile file2) {
        if (null != file1 && null != file2) {
            DiffRequestChain chain = DiffUtil.createMutableChainFromFiles(project, file1, file2);
            DiffManager.getInstance().showDiff(project, chain, DiffDialogHints.MODAL);
        }
    }

    public static void showDiffWithConfigLibFile(Project project) {
        VirtualFile remoteConfigLibFile = MedConfig.getRemoteConfigLibFile(project);
        VirtualFile localConfigLibFile = MedConfig.getLocalConfigLibFile(project);
        MedUtil.showDiff(project, localConfigLibFile, remoteConfigLibFile);
    }

    public static boolean checkShowDiff(Project project) {
        VirtualFile localConfigLibFile = MedConfig.getLocalConfigLibFile(project);
        VirtualFile remoteConfigLibFile = MedConfig.getRemoteConfigLibFile(project);
        if (null != localConfigLibFile && null != remoteConfigLibFile) {
            String localFileContent = MedUtil.getFileContent(localConfigLibFile);
            String remoteFileContent = MedUtil.getFileContent(remoteConfigLibFile);
            if (!localFileContent.equals(remoteFileContent)) {
                return true;
            }
        }
        return false;
    }

    public static void checkShowDiffAction(Project project) {
        if (!MedUtil.isRemoteConfigLibDirExist(project)) {
            Messages.showWarningDialog("未检测到远程仓库目录：x_dev/libs", "提示");
            return;
        }

        MedUtil.saveAll();
        CmdUtil.execRemoteConfigShellCmd(project, CmdUtil.CMD_PULL);
        if (MedUtil.checkShowDiff(project)) {
            MedUtil.showDiffWithConfigLibFile(project);
        } else {
            Messages.showInfoMessage("恭喜，本地依赖库与远程依赖库版本一致！！", "提示");
        }
    }

    public static boolean isRemoteConfigLibDirExist(Project project) {
        return new File(MedConfig.getRemoteConfigLibDir(project)).exists();
    }

    public static VirtualFile findTargetFile(Project project, String fileName, String filePath, VirtualFile targetFile) {

        if (null != targetFile && targetFile.isValid() && targetFile.getPath().contains(project.getBasePath())) {
            return targetFile;
        }

        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project));
        if (psiFiles.length == 0) {
            return null;
        }
        VirtualFile file = null;
        for (PsiFile psiFile : psiFiles) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (null != virtualFile && filePath.equals(virtualFile.getPath())) {
                file = virtualFile;
                break;
            }
        }
        targetFile = file;
        return targetFile;
    }

    public static boolean isEmpty(String str) {
        return null == str || str.replaceAll(" ", "").isEmpty();
    }

    public static void saveAll() {
        ApplicationManager.getApplication().runWriteAction(
                new Runnable() {
                    public void run() {
                        FileDocumentManager.getInstance().saveAllDocuments();
                    }
                });
    }

    public static Window getForemostWindow(Project project) {

        Window window = null;
        WindowManager windowManager = LoadingState.COMPONENTS_REGISTERED.isOccurred() ? WindowManager.getInstance() : null;
        if (windowManager != null) {
            window = windowManager.suggestParentWindow(project);
        }
        if (null != window) {
            return window;
        }


        Window _window = null;
        IdeFocusManager ideFocusManager = IdeFocusManager.getGlobalInstance();
        Component focusOwner = IdeFocusManager.findInstance().getFocusOwner();
        if (focusOwner != null) {
            _window = SwingUtilities.getWindowAncestor(focusOwner);
        }

        if (_window == null) {
            focusOwner = ideFocusManager.getLastFocusedFor(ideFocusManager.getLastFocusedIdeWindow());
            if (focusOwner != null) {
                _window = SwingUtilities.getWindowAncestor(focusOwner);
            }
        }

        if (_window == null) {
            _window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        }

        if (_window == null) {
            _window = WindowManager.getInstance().findVisibleFrame();
        }

        if (_window == null && window != null) {
            focusOwner = window.getMostRecentFocusOwner();
            if (focusOwner != null) {
                _window = SwingUtilities.getWindowAncestor(focusOwner);
            }
        }

        if (_window != null && ModalityHelper.isModalBlocked((Window) _window)) {
            _window = ModalityHelper.getModalBlockerFor((Window) _window);
        }

        while (_window != null && MacUtil.getWindowTitle((Window) _window) == null) {
            _window = ((Window) _window).getOwner();
        }

        while (Registry.is("skip.untitled.windows.for.mac.messages") && _window instanceof JDialog && !((JDialog) _window).isModal()) {
            _window = ((Window) _window).getOwner();
        }

        while (_window != null && ((Window) _window).getParent() != null && WindowManager.getInstance().isNotSuggestAsParent((Window) _window)) {
            _window = ((Window) _window).getOwner();
        }

        return (Window) _window;
    }

}
