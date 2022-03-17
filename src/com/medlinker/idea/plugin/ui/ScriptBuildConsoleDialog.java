package com.medlinker.idea.plugin.ui;

import com.google.gson.Gson;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.medlinker.idea.plugin.entity.PgyUploadResult;
import com.medlinker.idea.plugin.util.CommandRunnable;
import com.medlinker.idea.plugin.util.LogUtil;
import com.medlinker.idea.plugin.util.MedUtil;
import com.medlinker.idea.plugin.util.MyNotifier;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @autho zhangquan
 */
public class ScriptBuildConsoleDialog extends JDialog {
    private int width = 1200;
    private int height = 600;
    private final Color mBackground = new Color(30, 30, 30);
    private String mTitle;
    private MedJTextArea mContentView;
    private JLabel mResultView;

    private CommandRunnable mTask;
    private Project mProject;

    private JButton btn_copy, btn_qrcode;
    private PgyUploadResult pgyUploadResult;

    public ScriptBuildConsoleDialog(Window owner, String title) {
        super(owner, title, ModalityType.MODELESS);

        mTitle = title;
        mContentView = new MedJTextArea();
        mContentView.setBackground(mBackground);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //得到屏幕的尺寸
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        if (screenWidth >= 1000) {
            width = screenWidth - 300;
        }
        if (screenHeight >= 700) {
            height = screenHeight - 300;
        }

        JScrollPane scrollPane = new JScrollPane();
//        scrollPane.add(jTextArea);
        scrollPane.setViewportView(mContentView);
        scrollPane.setBounds(10, 10, width - 10, height - 10);


        Container container = getContentPane();
        container.add(scrollPane, BorderLayout.CENTER);
        container.setBackground(mBackground);

        Box box = Box.createVerticalBox();
        box.setBackground(mBackground);
        container.add(box, BorderLayout.SOUTH);

        mResultView = new JLabel();
        mResultView.setBackground(mBackground);
        box.add(Box.createVerticalStrut(5));
        box.add(mResultView);
        box.add(Box.createVerticalStrut(10));

        Box retryBar = Box.createHorizontalBox();
        retryBar.setBackground(mBackground);
        box.add(retryBar);
        box.add(Box.createVerticalStrut(10));

        JButton btn_close = new JButton("关闭");
        btn_copy = new JButton("复制二维码地址");
        btn_qrcode = new JButton("查看二维码图片");
        btn_copy.setVisible(false);
        btn_qrcode.setVisible(false);

        retryBar.add(btn_close);
        retryBar.add(Box.createHorizontalStrut(20));
        retryBar.add(btn_copy);
        retryBar.add(Box.createHorizontalStrut(20));
        retryBar.add(btn_qrcode);

        btn_close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (null != mTask) mTask.stop();
                dispose();
            }
        });
        btn_copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (null == pgyUploadResult) {
                    Messages.showWarningDialog("未找到二维码地址，麻烦从控制台中查找", "提示");
                    return;
                }

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection contents=new StringSelection(pgyUploadResult.data.qrcode);
                clipboard.setContents(contents, new ClipboardOwner(){

                    @Override
                    public void lostOwnership(Clipboard clipboard, Transferable contents) {

                    }
                });
                Messages.showInfoMessage("复制成功","提示");
//                MyNotifier.notify(mProject,"复制成功", NotificationType.INFORMATION);
            }
        });
        btn_qrcode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (null == pgyUploadResult) {
                    Messages.showWarningDialog("未找到二维码地址，麻烦从控制台中查找", "提示");
                    return;
                }
                new QrcodeDialog(MedUtil.getForemostWindow(mProject),mTitle, pgyUploadResult.data).setVisible(true);
            }
        });


        setSize(width, height);
        setLocationRelativeTo(null);
//        setVisible(true);
    }

    private void resetUI() {
        lastPos = 0;
        pgyUploadResult = null;
        mContentView.setText("");
        mResultView.setText("");
        btn_copy.setVisible(false);
        btn_qrcode.setVisible(false);
    }

    public void execute(Project project, CommandRunnable cmdr) {
        this.mTask = cmdr;
        this.mProject = project;
        resetUI();
        ProgressManager manager = ProgressManager.getInstance();
        manager.run(new Task.Backgroundable(project, mTitle) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                cmdr.run();
            }
        });
        show();
    }


    int lastPos = 0;

    public void onProgress(String text) {
        int curPosition = mContentView.getCaretPosition();
        mContentView.append(text + "\n");

        if (text.startsWith("{'code'")) {
            String json = text.replace("'", "\"");
            pgyUploadResult = new Gson().fromJson(json, PgyUploadResult.class);
        }

        if (curPosition == lastPos) {
            int nextPos = mContentView.getText().length();
            mContentView.setCaretPosition(nextPos); //自动滚动到底部
            lastPos = nextPos;
        }
    }

    public void onSuccess() {
        LogUtil.d(">>>onSuccess");
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                mResultView.setForeground(Color.GREEN);
                mResultView.setText(">>> 打包成功 <<<");
                if (null != pgyUploadResult) {
                    btn_copy.setVisible(true);
                    btn_qrcode.setVisible(true);
                }
//                        Messages.showInfoMessage(project, "打包成功", "温馨提示");
            }
        });
    }

    public void onFail() {
        LogUtil.d(">>>onFail");
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                mResultView.setForeground(Color.RED);
                mResultView.setText(">>> 打包失败 <<<");
                btn_copy.setVisible(true);
                btn_qrcode.setVisible(true);
//                Messages.showErrorDialog(project, e.getMessage(), "打包失败");
            }
        });
    }

}
