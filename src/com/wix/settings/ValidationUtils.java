package com.wix.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * Created by idok on 11/27/14.
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }


//    private boolean validatePath(String path, boolean allowEmpty) {
//        if (StringUtils.isEmpty(path)) {
//            return allowEmpty;
//        }
//        File filePath = new File(path);
//        if (filePath.isAbsolute()) {
//            if (!filePath.exists() || !filePath.isFile()) {
//                return false;
//            }
//        } else {
//            VirtualFile child = project.getBaseDir().findFileByRelativePath(path);
//            if (child == null || !child.exists() || child.isDirectory()) {
//                return false;
//            }
//        }
//        return true;
//    }

    public static boolean validatePath(Project project, String path, boolean allowEmpty) {
        if (StringUtils.isEmpty(path)) {
            return allowEmpty;
        }
        File filePath = new File(path);
        if (filePath.isAbsolute()) {
            if (!filePath.exists() || !filePath.isFile()) {
                return false;
            }
        } else {
            if (project == null || project.getBaseDir() == null) {
                return true;
            }
            VirtualFile child = project.getBaseDir().findFileByRelativePath(path);
            if (child == null || !child.exists() || child.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateDirectory(Project project, String path, boolean allowEmpty) {
        if (StringUtils.isEmpty(path)) {
            return allowEmpty;
        }
        File filePath = new File(path);
        if (filePath.isAbsolute()) {
            if (!filePath.exists() || !filePath.isDirectory()) {
                return false;
            }
        } else {
            VirtualFile child = project.getBaseDir().findFileByRelativePath(path);
            if (child == null || !child.exists() || !child.isDirectory()) {
                return false;
            }
        }
        return true;
    }
}
