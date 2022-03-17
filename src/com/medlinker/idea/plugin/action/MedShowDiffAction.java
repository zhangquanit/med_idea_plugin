package com.medlinker.idea.plugin.action;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.vfs.VirtualFile;
import com.medlinker.idea.plugin.MedConfig;
import org.jetbrains.annotations.NotNull;

/**
 * @autho zhangquan
 */
public class MedShowDiffAction extends LocalConfigFileAction {

    @Override
    public void update(@NotNull AnActionEvent e) {

        boolean canShow = false;
        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (null != files && files.length > 0) {
            for (VirtualFile file : files) {
                if (file.getPath().equals(MedConfig.getLocalConfigLibFilePath(e.getProject()))) {
                    canShow = true;
                    break;
                }
            }
        }
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(canShow);
        if (ActionPlaces.isPopupPlace(e.getPlace())) {
            presentation.setVisible(canShow);
        }
    }
}
