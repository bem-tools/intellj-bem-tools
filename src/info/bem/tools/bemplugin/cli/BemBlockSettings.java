package info.bem.tools.bemplugin.cli;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by h4 on 12/12/15.
 */
public class BemBlockSettings {
    public String node;
    public String jscsExecutablePath;
    public String cwd;
    public String targetFile;
    public String bemBlockExecutablePath;

    public static BemBlockSettings build(@NotNull String cwd, @NotNull String nodeInterpreter,
                                         @NotNull String bemBlockBin) {
        BemBlockSettings settings = new BemBlockSettings();
        settings.cwd = cwd;
        settings.bemBlockExecutablePath = bemBlockBin;
        settings.node = nodeInterpreter;
        return settings;
    }
}
