package info.bem.tools.bemplugin.cli;

/**
 * Created by melikhov on 15/11/16.
 */

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.wix.nodejs.NodeFinder;
import com.wix.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public final class BemFinder {
    public static final String BEMRC = ".bemrc";
    public static final String BEM_BASE_NAME = "bem-tools-create";

    private BemFinder() {
    }


    @NotNull
    public static List<File> searchForBemBin(File projectRoot) {
        return NodeFinder.searchAllScopesForBin(projectRoot, BEM_BASE_NAME);
    }

}