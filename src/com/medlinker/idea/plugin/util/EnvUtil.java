package com.medlinker.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.util.EnvironmentUtil;

import java.util.Map;

/**
 * @autho zhangquan
 */
public class EnvUtil {
    public static String getPython3Bin(Project project) {
        String python3 = "";

        //1、通过which命令查找
        String bin = "which";
        String command = "python3";
        CommandRunnable cmdr = new CommandRunnable(project, bin, null);
        cmdr.setCommand(command);
        cmdr.saveOutput(true);
        cmdr.run();
        String output = cmdr.getOutput();
        if (!MedUtil.isEmpty(output) && output.contains(command)) {
            python3 = output.replace("\n", "");
            return python3;
        }

        //通过环境配置查找
        Map<String, String> environmentMap = EnvironmentUtil.getEnvironmentMap();
        for (Map.Entry<String, String> entry : environmentMap.entrySet()) {
            if (entry.getKey().equals("PATH")) {
                String value = entry.getValue();
                String[] envs = value.split(":");
                for (String item : envs) {
                    if (item.contains(command)) {
                        python3 = item.replace("\n", "").replace(" ","");
                        return python3;
                    }
                }
                break;
            }
        }
        return python3;
    }
}
