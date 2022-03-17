package com.medlinker.idea.plugin.git;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.io.File;

public class GitVcsSettings implements PersistentStateComponent<GitVcsSettings> {
    public static final String DEFAULT_CYGWIN_GIT_EXEC = "C:\\cygwin\\bin\\git.exe";
    public static final String DEFAULT_MSYS_GIT_EXEC = "C:\\Program Files\\Git\\bin\\git.exe";
    public static final String DEFAULT_LOCAL_GIT_EXEC = "/usr/local/bin/git";
    public static final String DEFAULT_UNIX_GIT_EXEC = "/usr/bin/git";
    public static final String DEFAULT_GIT_EXEC = "git";
    public String GIT_EXECUTABLE = defaultGit();

    @Override
    public GitVcsSettings getState() {
        return this;
    }

    @Override
    public void loadState(GitVcsSettings gitVcsSettings) {
        XmlSerializerUtil.copyBean(gitVcsSettings, this);
    }

    public static GitVcsSettings getInstance(Project project) {
//        return ServiceManager.getService(project, GitVcsSettings.class);
        return new GitVcsSettings();
    }

    private String defaultGit() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            File exe = new File(DEFAULT_CYGWIN_GIT_EXEC);   // Look for Cygwin Git first
            if (exe.exists()) return exe.getAbsolutePath();
            exe = new File(DEFAULT_MSYS_GIT_EXEC);          // Look for Msys Git second
            if (exe.exists()) return exe.getAbsolutePath();
        } else {
            File exe = new File(DEFAULT_UNIX_GIT_EXEC);
            if (exe.exists()) return exe.getAbsolutePath();
            exe = new File(DEFAULT_UNIX_GIT_EXEC.replace("usr","opt"));
            if (exe.exists()) return exe.getAbsolutePath();
            exe = new File(DEFAULT_LOCAL_GIT_EXEC);
            if (exe.exists()) return exe.getAbsolutePath();
            exe = new File(DEFAULT_LOCAL_GIT_EXEC.replace("usr","opt"));
            if (exe.exists()) return exe.getAbsolutePath();
        }
        return DEFAULT_GIT_EXEC;     // otherwise, hope it's in $PATH
    }
}