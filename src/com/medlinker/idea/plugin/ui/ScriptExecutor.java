package com.medlinker.idea.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.medlinker.idea.plugin.util.*;

import java.io.File;

/**
 * @autho zhangquan
 */
public class ScriptExecutor {
    private ScriptBuildConsoleFrame mConsole;
    private CommandRunnable mTask;

    public void buildApp(Project project, boolean online) {
        String gradleBuildFilePath = project.getBasePath() + File.separator + "gradlew";
        File gradleBuildFile = new File(gradleBuildFilePath);
        if (!gradleBuildFile.exists()) {
            Messages.showWarningDialog("未检测到gradlew打包脚本，不支持打包", "提示");
            return;
        }
        File xScriptsDir = new File(project.getBasePath() + File.separator + "x_scripts");
        File scriptsDir = new File(project.getBasePath() + File.separator + "scripts");
        if (!scriptsDir.exists() && !xScriptsDir.exists()) {
            Messages.showWarningDialog("未检测到scripts/x_scripts目录，不支持打包", "提示");
            return;
        }

        String progressTitle = online ? "正式环境包" : "测试环境包";

        if (null != mConsole && !mConsole.isDisposed()) {
            Messages.showInfoMessage("当前已有任务窗口，请打开已有窗口查看。", "提示");
            return;
        }


        mConsole = new ScriptBuildConsoleFrame(progressTitle);
//        ScriptBuildConsoleDialog console = new ScriptBuildConsoleDialog(MedUtil.getForemostWindow(project), progressTitle);

//        if (null == mTask) {
        String bin = EnvUtil.getPython3Bin(project);
        LogUtil.d("python3=" + bin);
        if (MedUtil.isEmpty(bin)) {
            Messages.showWarningDialog("未找到python3", "提示");
            return;
        }

        mTask = new CommandRunnable(project, bin, new OnProcessListener() {
            @Override
            public void onProcess(String text) {
                mConsole.onProgress(text);
            }

            @Override
            public void onSuccess() {
                mConsole.onSuccess();
            }

            @Override
            public void onFail(Exception e) {
                mConsole.onFail();
            }
        });
        if (xScriptsDir.exists()) {
            mTask.setCommand("x_scripts" + File.separator + "releaseApk.py");
        } else if (scriptsDir.exists()) {
            mTask.setCommand("scripts" + File.separator + "releaseApk.py");
        }
        if (online) {
            mTask.setArgs(new String[]{"-e", "online"});
        }
//        }
//        if (mTask.getStatus() != TaskStatus.RUNNING) {
        mConsole.execute(project, mTask);
//        }


    }

}
