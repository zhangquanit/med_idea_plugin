package com.medlinker.idea.plugin.git;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.diff.DiffProvider;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.rollback.RollbackEnvironment;
import com.medlinker.idea.plugin.util.LogUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * @autho zhangquan
 */
public class GitVcs extends AbstractVcs {
    private static final String GIT = "Git";
//    private ChangeProvider changeProvider;
    private VcsShowConfirmationOption addConfirmation;
    private VcsShowConfirmationOption delConfirmation;

    private CheckinEnvironment checkinEnvironment;
    private RollbackEnvironment rollbackEnvironment;
//    private GitUpdateEnvironment updateEnvironment;

//    private GitAnnotationProvider annotationProvider;
    private DiffProvider diffProvider;
    private VcsHistoryProvider historyProvider;
    private Disposable activationDisposable;
    private final ProjectLevelVcsManager vcsManager;
    private final GitVcsSettings settings;
    private EditorColorsScheme editorColorsScheme;
    private Configurable configurable;
//    private RevisionSelector revSelector;
//    private GitVirtualFileAdapter gitFileAdapter;
//    private RefactoringElementListenerProvider renameListenerProvider;

    public GitVcs(@NotNull Project project) {
        super(project, GIT);
        vcsManager = ProjectLevelVcsManager.getInstance(project);
        settings = GitVcsSettings.getInstance(project);
        addConfirmation = vcsManager.getStandardConfirmation(VcsConfiguration.StandardConfirmation.ADD, this);
        delConfirmation = vcsManager.getStandardConfirmation(VcsConfiguration.StandardConfirmation.REMOVE, this);
//        changeProvider = gitChangeProvider;
//        checkinEnvironment = gitCheckinEnvironment;
//        annotationProvider = gitAnnotationProvider;
//        diffProvider = gitDiffProvider;
        editorColorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
//        historyProvider = gitHistoryProvider;
//        rollbackEnvironment = gitRollbackEnvironment;
//        revSelector = new GitRevisionSelector();
//        configurable = new GitVcsConfigurable(settings, myProject);
//        updateEnvironment = new GitUpdateEnvironment(myProject, settings, configurable);
//
//        ((GitCheckinEnvironment) checkinEnvironment).setProject(myProject);
//        ((GitCheckinEnvironment) checkinEnvironment).setSettings(settings);
//        renameListenerProvider = new GitRefactoringListenerProvider();
    }

    public static GitVcs getInstance(@NotNull Project project) {
//        return (GitVcs) ProjectLevelVcsManager.getInstance(project).findVcsByName(GIT);
        return new GitVcs(project);
    }



    @NotNull
    @Override
    public String getDisplayName() {
        return GIT;
    }

    @Override
    public Configurable getConfigurable() {
        return null;
    }

    public void showErrors(@NotNull java.util.List<VcsException> list, @NotNull String action) {
        if (list.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("\n");
            buffer.append(action).append(" Error: ");
            VcsException e;
            for (Iterator<VcsException> iterator = list.iterator(); iterator.hasNext(); buffer.append(e.getMessage())) {
                e = iterator.next();
                buffer.append("\n");
            }
            String msg = buffer.toString();
            showMessage(msg);
            Messages.showErrorDialog(myProject, msg, "Error");
        }
    }

    public void showMessages(@NotNull String message) {
        if (message.length() == 0)
            return;
        showMessage(message);
    }

    @NotNull
    public GitVcsSettings getSettings() {
        return settings;
    }

    private void showMessage(@NotNull String message) {
        LogUtil.d(message);
    }
}
