package com.medlinker.idea.plugin.ui;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.medlinker.idea.plugin.entity.PgyUploadResult;
import com.medlinker.idea.plugin.util.CommandRunnable;
import com.medlinker.idea.plugin.util.LogUtil;
import com.medlinker.idea.plugin.util.MockUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @autho zhangquan
 */
public class ScriptBuildConsoleFrame extends JFrame {
    private int width = 1200;
    private int height = 600;
    private final Color mBackground = new Color(30, 30, 30);
    private String mTitle;
    private MedJTextArea mContentView;
    private JLabel mResultView;

    private CommandRunnable mTask;
    private Project mProject;

    private JButton btn_reActoion, btn_copy, btn_qrcode;
    private boolean isDisposed;
    private boolean isTaskFinished;
    private PgyUploadResult pgyUploadResult;

    public ScriptBuildConsoleFrame(String title) {
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


        setTitle(title);
        setBackground(mBackground);
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
        btn_reActoion = new JButton("重新执行");
        btn_copy = new JButton("复制二维码地址");
        btn_qrcode = new JButton("查看二维码图片");
        btn_reActoion.setVisible(false);
        btn_copy.setVisible(false);
        btn_qrcode.setVisible(false);

        retryBar.add(btn_close);
        retryBar.add(Box.createHorizontalStrut(20));
        retryBar.add(btn_reActoion);
        retryBar.add(Box.createHorizontalStrut(20));
        retryBar.add(btn_copy);
        retryBar.add(Box.createHorizontalStrut(20));
        retryBar.add(btn_qrcode);

        btn_close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isDisposed = true;
                stopTask();
                dispose();
            }
        });
        btn_reActoion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopTask();
                execute(mProject, mTask);
            }
        });
        btn_copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (null == pgyUploadResult) {
                    JOptionPane.showMessageDialog(getOwner(), "未找到二维码地址，麻烦从控制台中查找", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String text = pgyUploadResult.data.qrcode;
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection contents = new StringSelection(text);
                clipboard.setContents(contents, new ClipboardOwner() {

                    @Override
                    public void lostOwnership(Clipboard clipboard, Transferable contents) {

                    }
                });
//                Messages.showInfoMessage("复制成功", "提示");
                JOptionPane.showMessageDialog(getOwner(), "复制成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btn_qrcode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (null == pgyUploadResult) {
//                    Messages.showWarningDialog("未找到二维码地址，麻烦从控制台中查找", "提示");
                    JOptionPane.showMessageDialog(getOwner(), "未找到二维码地址，麻烦从控制台中查找", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                new QrcodeDialog(getOwner(), mTitle, pgyUploadResult.data).setVisible(true);
            }
        });


        pack();
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                isDisposed = true;
                stopTask();
            }

        });


        Toolkit toolkit = getToolkit();
        Image image = toolkit.createImage("/META-INF/icon.png");
        setIconImage(image);

//        setAlwaysOnTop(true);

        setVisible(true);

    }

    private void resetUI() {
        mContentView.setText("");
        mResultView.setText("");

        btn_reActoion.setVisible(false);
        btn_copy.setVisible(false);
        btn_qrcode.setVisible(false);
    }

    public void execute(Project project, CommandRunnable cmdr) {
        this.mTask = cmdr;
        this.mProject = project;
        this.pgyUploadResult = null;
        this.isTaskFinished = false;
        this.lastPos = 0;
        resetUI();
        ProgressManager manager = ProgressManager.getInstance();
        manager.run(new Task.Backgroundable(project, mTitle) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                cmdr.run();
            }
        });

    }

    private void stopTask() {
        if (null != mTask) {
            mTask.stop();
        }
    }


    int lastPos = 0;

    public void onProgress(String text) {
        if (isDisposed) return;
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
        isTaskFinished = true;
        if (isDisposed) return;
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                mResultView.setForeground(Color.GREEN);
                mResultView.setText(">>> 打包成功 <<<");
                if (null != pgyUploadResult) {
                    btn_copy.setVisible(true);
                    btn_qrcode.setVisible(true);
                }
            }
        });
    }

    public void onFail() {
        LogUtil.d(">>>onFail");
        isTaskFinished = true;
        if (isDisposed) return;
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                mResultView.setForeground(Color.RED);
                mResultView.setText(">>> 打包失败 <<<");
                btn_reActoion.setVisible(true);
                btn_copy.setVisible(false);
                btn_qrcode.setVisible(false);

//                mockData()
            }
        });

    }

    private void mockData() {
        pgyUploadResult = MockUtil.mockQrcodeData();
        btn_copy.setVisible(true);
        btn_qrcode.setVisible(true);
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    public boolean isTaskFinished() {
        return isTaskFinished;
    }


}
