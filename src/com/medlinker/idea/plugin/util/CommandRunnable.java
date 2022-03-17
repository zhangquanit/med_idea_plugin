package com.medlinker.idea.plugin.util;
/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * Copyright 2008 MQSoftware
 * Author: Mark Scott
 *
 * This code was originally derived from the MKS & Mercurial IDEA VCS plugins
 */

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.util.EnvironmentUtil;
import com.medlinker.idea.plugin.entity.TaskStatus;
import com.medlinker.idea.plugin.git.GitVcs;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Run a Git command as a Runnable
 */
@SuppressWarnings({"JavaDoc"})
public class CommandRunnable implements Runnable {
    private static final int BUF_SIZE = 4096;
    private String cmd = null;
    private Project project = null;
    private String[] opts = null;
    private String[] args = null;
    private Exception exception = null;
    private boolean keepit = false;
    private boolean silent = false;
    private String bin;
    private File directory;
    private StringBuffer outputs = null;
    private Process proc;
    private OnProcessListener onProcessListener;
    private TaskStatus taskStatus = TaskStatus.PENDING;

    public CommandRunnable(@NotNull final Project project, String bin, OnProcessListener onProcessListener) {
        this.project = project;
        this.bin = bin;
        this.directory = new File(project.getBasePath());
        this.onProcessListener = onProcessListener;
    }

    public CommandRunnable(@NotNull final Project project, String bin,
                           String cmd, String[] opts, String[] args) {
        this.project = project;
        this.bin = bin;
        this.cmd = cmd;
        this.opts = opts;
        this.args = args;
        this.directory = new File(project.getBasePath());
    }

    @SuppressWarnings({"EmptyCatchBlock"})
    @Override
    public void run() {
        if (bin == null) throw new IllegalStateException("No bin set!");
        exception = null;
        taskStatus = TaskStatus.RUNNING;

        GitVcs vcs = GitVcs.getInstance(project);

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(bin);
        if (!MedUtil.isEmpty(cmd)) cmdLine.add(cmd);
        if (opts != null && opts.length > 0)
            cmdLine.addAll(Arrays.asList(opts));
        if (args != null && args.length > 0)
            cmdLine.addAll(Arrays.asList(args));

//        ProgressManager manager = ProgressManager.getInstance();
//        ProgressIndicator indicator = manager.getProgressIndicator();
//        indicator.setText(bin + " " + cmd + "...");
//        indicator.setIndeterminate(true); //显示进度条

        String cmdStr = StringUtil.join(cmdLine, " ");
        vcs.showMessages(bin + " " + cmdStr.substring(bin.length()));

        ProcessBuilder pb = new ProcessBuilder(cmdLine);
        Map<String, String> pbenv = pb.environment();
        pbenv.putAll(EnvironmentUtil.getEnvironmentMap());
        pb.directory(directory);
        pb.redirectErrorStream(true);


        BufferedReader in = null;
        int exitValue = -1;
        outputs = new StringBuffer();
        try {
            proc = pb.start();
            Thread.sleep(250);
            in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line = null;
            while ((line = in.readLine()) != null) {
//                if(line.contains("fatal: not a git repository")){
//                    continue;
//                }
                if (keepit)
                    outputs.append(line).append("\n");
                if (!silent)
                    vcs.showMessages(line);
                if (null != onProcessListener) {
                    onProcessListener.onProcess(line);
                }
            }
            exitValue = proc.waitFor();
        } catch (InterruptedException ie) {
        } catch (Exception e) {
            exception = new VcsException(e);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e) {
            }

        }

        if (exitValue != 0 && null == exception) {
            String msg = outputs.toString();
            int nullIdx = msg.indexOf(0);
            if (nullIdx > 5)
                msg = msg.substring(0, nullIdx);
            exception = new VcsException(msg);
        }

        if (null != onProcessListener) {
            if (null != exception) {
                onProcessListener.onFail(exception);
            } else {
                if (null != onProcessListener) {
                    onProcessListener.onSuccess();
                }
            }
        }
        taskStatus = TaskStatus.FINISHED;
    }

    /**
     * Returns the exception thrown by the command runnable
     *
     * @return The exception, else null if the command was sucessful
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Set the bin
     */
    public void setBin(String bin) {
        this.bin = bin;
    }

    /**
     * Set the runnable's Git command.
     */
    public void setCommand(String cmd) {
        this.cmd = cmd;
    }


    /**
     * Set the runnable's Git command options.
     */
    public void setOptions(String[] opts) {
        this.opts = opts;
    }

    /**
     * Set the runnable's Git command arguments.
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * 设置命令执行目录
     *
     * @param directory
     */
    public void setDirectory(File directory) {
        this.directory = directory;
    }

    /**
     * Set to true if a copy of the git command output should be saved. Use getOutput()
     * later to retrieve it. (Default is false)
     */
    public void saveOutput(boolean keepit) {
        this.keepit = keepit;
    }

    /**
     * Set to true if git command output is to NOT be sent the version control console. (Default is false)
     */
    public void setSilent(boolean isSilent) {
        silent = isSilent;
    }

    public void addOnProcessListener(OnProcessListener listener) {
        onProcessListener = listener;
    }

    /**
     * Retrieve the output (error & stdout are mingled) from the git command. This is only useful after the command has finished running...
     */
    public String getOutput() {
        if (!keepit || null == outputs)
            return null;
        return outputs.toString();
    }

    public void stop() {
        try {
            if (null != proc) {
                proc.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        taskStatus = TaskStatus.FINISHED;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }


}