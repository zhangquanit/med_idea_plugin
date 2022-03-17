package com.medlinker.idea.plugin.util;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.ErrorDiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWithoutContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

import static com.intellij.diff.DiffRequestFactoryImpl.getContentTitle;
import static com.intellij.diff.DiffRequestFactoryImpl.getTitle;
import static com.intellij.vcsUtil.VcsUtil.getFilePath;

/**
 * @autho zhangquan
 */
public class DiffUtil {

    public static DiffRequestChain getDiffRequestChain(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        DiffRequest diffRequest = getDiffRequest(e);
        LogUtil.d("getDiffRequestChain diffRequest=" + diffRequest);
        if (diffRequest != null) {
            return new SimpleDiffRequestChain(diffRequest);
        } else {
            VirtualFile[] data = (VirtualFile[]) e.getRequiredData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
            VirtualFile file1;
            VirtualFile file2;
            if (data.length == 1) {
                file1 = data[0];
                file2 = getOtherFile(project, file1);
                if (file2 == null || !hasContent(file2)) {
                    return null;
                }
            } else {
                file1 = data[0];
                file2 = data[1];
            }

            if (file1.isValid() && file2.isValid()) {
//                CompareFilesAction.Type type1 = getType(file1);
//                CompareFilesAction.Type type2 = getType(file2);
//                if (type1 == CompareFilesAction.Type.DIRECTORY || type2 == CompareFilesAction.Type.DIRECTORY) {
//                    FeatureUsageTracker.getInstance().triggerFeatureUsed("dir.diff");
//                }
//
//                if (type1 == CompareFilesAction.Type.ARCHIVE || type2 == CompareFilesAction.Type.ARCHIVE) {
//                    FeatureUsageTracker.getInstance().triggerFeatureUsed("jar.diff");
//                }

                return createMutableChainFromFiles(project, file1, file2);
            } else {
                return null;
            }
        }
    }


    public static MutableDiffRequestChain createMutableChainFromFiles(@Nullable Project project, @NotNull VirtualFile file1, @NotNull VirtualFile file2) {
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
        DiffContent content1 = contentFactory.create(project, file1);
        DiffContent content2 = contentFactory.create(project, file2);
        MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);
//        DiffRequestFactory requestFactory = DiffRequestFactory.getInstance();
//        chain.setWindowTitle(requestFactory.getTitle(file1, file2));
//        chain.setTitle1(requestFactory.getContentTitle(file1));
//        chain.setTitle2(requestFactory.getContentTitle(file2));
        chain.setWindowTitle("依赖库文件比较");
        chain.setTitle1("本地文件");
        chain.setTitle2("远程依赖库文件");
        return chain;
    }

    public static DiffRequest getDiffRequest(@NotNull AnActionEvent e) {
        VirtualFile[] data = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        VirtualFile left = data[0];
        VirtualFile right;

        LogUtil.d("getDiffRequest data.length=" + data.length);

        if (data.length < 2) {
            right = getOtherFile(e.getProject(), data[0]);

//            if (right == null || !hasContent(right) || !isJsonVirtualFile(right)) {
//                return new ErrorDiffRequest("Problem with second JSON file");
//            }
//
//            if (!data[0].isValid()) {
//                return new ErrorDiffRequest("Problem with first JSON file"); // getOtherFile() shows dialog that can invalidate this file
//            }
        } else {
            right = data[1];
        }

        try {
            return new SimpleDiffRequest(mainTitle(left, right), content(left), content(right), contentTitle(left), contentTitle(right));
        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
            return new ErrorDiffRequest("Problem with some of JSON files");
        }
    }


    private static DiffContent content(VirtualFile file) throws IOException {
        return new DocumentContentImpl(new DocumentImpl(MedUtil.getFileContent(file)));
    }

    private static String contentTitle(VirtualFile file) {
        return getContentTitle(getFilePath(file));
    }

    private static String mainTitle(VirtualFile file1, VirtualFile file2) {
        FilePath path1 = file1 != null ? getFilePath(file1) : null;
        FilePath path2 = file2 != null ? getFilePath(file2) : null;

        return getTitle(path1, path2, " vs ");
    }

    private static boolean isJsonVirtualFile(VirtualFile file) {
        return file != null && file.isValid() && !file.isDirectory() && "json".compareToIgnoreCase(Objects.requireNonNull(file.getExtension())) == 0;
    }

    private static VirtualFile getOtherFile(Project project, VirtualFile file) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, true, true, false);

        return FileChooser.chooseFile(descriptor, project, file);
    }

    private static boolean hasContent(VirtualFile file) {
        return !(file instanceof VirtualFileWithoutContent);
    }


}
