package com.medlinker.idea.plugin.ui;

import com.intellij.openapi.ui.ex.MultiLineLabel;
import com.medlinker.idea.plugin.entity.PgyUploadResult;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @autho zhangquan
 */
public class QrcodeDialog extends JDialog {
    private PgyUploadResult.UploadData data;
    private String mBuildEnv;

    public QrcodeDialog(Window owner, String title, PgyUploadResult.UploadData data) {
        super(owner, "二维码", ModalityType.TOOLKIT_MODAL);
        this.data = data;
        this.mBuildEnv = "Android" + title;
        init();
    }

    private void init() {
        Container container = getContentPane();
//        setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());


        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int leftMargin = 40, rightMargin = 40;
        int topMargin = 30, bottomMargin = 30;
        int windowWidth = leftMargin + 258 + leftMargin;
        int windowHeight = 480;

        Box verticalBox = Box.createVerticalBox();

        //标题
        JLabel tv_title = new JLabel(data.appName + "  " + mBuildEnv,JLabel.CENTER);
        tv_title.setFont(new Font(null, Font.BOLD, 18));
        tv_title.setForeground(Color.RED);
        verticalBox.add(tv_title);
        verticalBox.add(Box.createVerticalStrut(10));

        //二维码
        ImageIcon imageIcon = null; //258x258
        try {
            imageIcon = new ImageIcon(new URL(data.qrcode));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        verticalBox.add(jLabel);
        verticalBox.add(Box.createVerticalStrut(10));


        //
        JLabel tv_version = new JLabel();
        tv_version.setText("版本：" + data.appVersion+"(build "+data.buildBuildVersion+")");
        verticalBox.add(tv_version);
        verticalBox.add(Box.createVerticalStrut(10));

        //
        JLabel tv_buildTime = new JLabel();
        tv_buildTime.setText("更新时间：" + data.buildTime);
        verticalBox.add(tv_buildTime);
        verticalBox.add(Box.createVerticalStrut(10));

        //
        MultiLineLabel tv_description = new MultiLineLabel();
        tv_description.setText(data.description);
        verticalBox.add(tv_description);
        verticalBox.add(Box.createVerticalStrut(10));

//        container.add(verticalBox);

        Box top = Box.createVerticalBox();
        top.add(Box.createVerticalStrut(topMargin));
        container.add(top, BorderLayout.NORTH);

        container.add(verticalBox, BorderLayout.CENTER);

        Box bottom = Box.createVerticalBox();
        bottom.add(Box.createVerticalStrut(bottomMargin));
        container.add(bottom, BorderLayout.SOUTH);

        Box left = Box.createVerticalBox();
        left.add(Box.createHorizontalStrut(leftMargin));
        container.add(left, BorderLayout.WEST);

        Box right = Box.createVerticalBox();
        right.add(Box.createHorizontalStrut(rightMargin));
        container.add(right, BorderLayout.EAST);


        setBounds(0, 0, windowWidth, windowHeight);
        //        setLocation(screenWidth / 2 - windowWidth / 2, 100);//设置窗口居中显示
        setLocationRelativeTo(null);
    }

    private byte[] getImage(String path) {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            URL url = new URL(path);
            bis = new BufferedInputStream(url.openStream());

            int len;
            byte[] buffer = new byte[1024];
            while ((len = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bis) bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }
}
