package com.medlinker.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.medlinker.idea.plugin.MedConfig;
import com.medlinker.idea.plugin.git.GitCommand;
import com.medlinker.idea.plugin.git.GitVcsSettings;
import com.medlinker.idea.plugin.ui.ScriptBuildConsoleFrame;

import java.io.File;

/**
 * @autho zhangquan
 */
public final class CmdUtil {
    public static final String CMD_PULL = "PULL";
    public static final String CMD_DIFF = "DIFF";
    public static final String CMD_PUSH = "PUSH";

    public static String execRemoteConfigShellCmd(Project project, String cmd) {
        if (!MedUtil.isRemoteConfigLibDirExist(project)) {
            return "";
        }

        VirtualFile root = MedConfig.getRemoteConfigLibFile(project);
        GitCommand command = new GitCommand(project, GitVcsSettings.getInstance(project), root);
        String repoURL = MedConfig.CONFIG_LIB_GIT;
        String result = "";
        try {
            switch (cmd) {
                case CMD_PULL:
                    command.pull(repoURL, true);
                    break;
                case CMD_DIFF:
                    command.pull(repoURL, true);
                    result = command.diff();
                    break;
                case CMD_PUSH:
                    VirtualFile[] files = new VirtualFile[]{root};
                    command.add(files);
                    command.commit(files, MedConfig.CONFIG_LIB_GIT_COMMIT);
                    command.push();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void buildApp(Project project, boolean online) {
        String gradleBuildFilePath = project.getBasePath() + File.separator + "gradlew";
        File gradleBuildFile = new File(gradleBuildFilePath);
        if (!gradleBuildFile.exists()) {
            MyNotifier.notify(project, "未检测到gradlew打包脚本，不支持打包");
            return;
        }
        File xScriptsDir = new File(project.getBasePath() + File.separator + "x_scripts");
        File scriptsDir = new File(project.getBasePath() + File.separator + "scripts");
        if (!scriptsDir.exists() && !xScriptsDir.exists()) {
            MyNotifier.notify(project, "未检测到scripts/x_scripts目录，不支持打包");
            return;
        }

        String progressTitle = online ? "正式环境包" : "测试环境包";
        ScriptBuildConsoleFrame console = new ScriptBuildConsoleFrame(progressTitle);
//        ScriptBuildConsoleDialog console = new ScriptBuildConsoleDialog(MedUtil.getForemostWindow(project), progressTitle);
        String bin = EnvUtil.getPython3Bin(project);
        LogUtil.d("python3=" + bin);
        CommandRunnable cmdr = new CommandRunnable(project, bin, new OnProcessListener() {
            @Override
            public void onProcess(String text) {
                console.onProgress(text);
            }

            @Override
            public void onSuccess() {
                console.onSuccess();
            }

            @Override
            public void onFail(Exception e) {
                console.onFail();
            }
        });
        if (xScriptsDir.exists()) {
            cmdr.setCommand("x_scripts" + File.separator + "releaseApk.py");
        } else if (scriptsDir.exists()) {
            cmdr.setCommand("scripts" + File.separator + "releaseApk.py");
        }
        if (online) {
            cmdr.setArgs(new String[]{"-e", "online"});
        }

        console.execute(project, cmdr);
    }

}
