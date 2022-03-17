package com.medlinker.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.medlinker.idea.plugin.entity.PgyUploadResult;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @autho zhangquan
 */
public class MockUtil {
    public static void refelectMethod(Project project, Class cls, String methodPrefix) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            Method[] declaredMethods = cls.getDeclaredMethods();
            for (Method method : declaredMethods) {
                method.setAccessible(true);
                if (method.getName().startsWith(methodPrefix)) {
                    stringBuffer.append(method.getName()).append("\n");
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (null != parameterTypes) {
                        String params = Arrays.toString(parameterTypes);
                        stringBuffer.append("params=" + params).append("\n");
                    }
                }
            }
        } catch (Exception e) {

        }
        System.out.println(stringBuffer);
        MyNotifier.notify(project, stringBuffer.toString());
    }

    public static void reflectGetMethods(Object obj) {
        try {
            Class aClass = obj.getClass();
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                method.setAccessible(true);
                if (method.getName().startsWith("get")) {
                    Object value = null;
                    try {
                        value = method.invoke(obj);
                    } catch (Exception e) {

                    }
                    System.out.println(method.getName() + "=" + value);
                }
            }
        } catch (Exception e) {
            System.out.println("出错了 msg=" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static PgyUploadResult mockQrcodeData() {
        PgyUploadResult pgyUploadResult = new PgyUploadResult();
        pgyUploadResult.data = new PgyUploadResult.UploadData();
        pgyUploadResult.data.appName = "骨医生";
        pgyUploadResult.data.appVersion = "1.0.0";
        pgyUploadResult.data.buildTime = "2022-02-25 22:12:30";
        pgyUploadResult.data.description = "测试环境,\nAndroid分支：master";
        pgyUploadResult.data.buildBuildVersion = "15";
        pgyUploadResult.data.qrcode = "https://www.pgyer.com/app/qrcodeHistory/a39fa82540597e80a8ff62c3a6bac030c1d483e6fa1ca6301a40c5fe5bf9c323";
        return pgyUploadResult;
    }

    public static void mockUI(Project project) {
        JFrame jFrame = new JFrame();
        JButton btn1 = new JButton("one");
        JButton btn2 = new JButton("two");
        JButton btn3 = new JButton("three");
        JButton btn4 = new JButton("four");
        JButton btn5 = new JButton("five");


        String title = "";

        //流式布局
//        title="流式布局";
//        jFrame.setLayout(new FlowLayout(FlowLayout.LEFT,10,5)); //默认为居中;水平间距10，垂直间距5
//        jFrame.add(btn1);
//        jFrame.add(btn2);
//        jFrame.add(btn3);
//        jFrame.add(btn4);
//        jFrame.add(btn5);

        //边界布局
//        title="边界布局";
//        jFrame.setLayout(new BorderLayout(10,5)); //默认为0，0；水平间距10，垂直间距5
//        jFrame.add(btn1,BorderLayout.EAST);
//        jFrame.add(btn2,BorderLayout.SOUTH);
//        jFrame.add(btn3,BorderLayout.WEST);
//        jFrame.add(btn4,BorderLayout.NORTH);
//        jFrame.add(btn5,BorderLayout.CENTER);

        //网格布局
//        title="网格布局";
//        jFrame.setLayout(new GridLayout(2,3,10,5)); //默认为1行，n列；2行3列，水平间距10，垂直间距5
//        jFrame.add(btn1);
//        jFrame.add(btn2);
//        jFrame.add(btn3);
//        jFrame.add(btn4);
//        jFrame.add(btn5);


        title = "盒子布局";
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
//        jFrame.add(btn1);
//        jFrame.add(btn2);
////        jFrame.getContentPane().add(Box.createHorizontalStrut(10)); //采用x布局时，添加固定宽度组件隔开
//        jFrame.getContentPane().add(Box.createVerticalStrut(50)); //采用y布局时，添加固定高度组件隔开
//        jFrame.add(btn3);
//        jFrame.add(btn4);
//        jFrame.add(btn5);

        //可以使用Box容器代替
        Box box = Box.createVerticalBox();
        box.add(btn1);
        box.add(Box.createVerticalStrut(10));
        box.add(btn2);
        box.add(Box.createVerticalStrut(10));
        box.add(btn3);
        box.add(Box.createVerticalStrut(10));
        box.add(btn4);
        box.add(btn5);
        box.add(Box.createVerticalStrut(50));

        Box subBox = Box.createHorizontalBox();
        subBox.add(btn4);
        subBox.add(Box.createHorizontalStrut(20));
        subBox.add(btn5);
        box.add(subBox);

        jFrame.add(box);


        jFrame.setTitle(title);
        jFrame.setResizable(true);
        jFrame.setSize(1000, 500);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setVisible(true);
    }

}
