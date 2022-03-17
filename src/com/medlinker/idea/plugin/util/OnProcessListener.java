package com.medlinker.idea.plugin.util;

/**
 * @autho zhangquan
 */
public interface OnProcessListener {
    void onProcess(String text);

    void onSuccess();

    void onFail(Exception e);
}
