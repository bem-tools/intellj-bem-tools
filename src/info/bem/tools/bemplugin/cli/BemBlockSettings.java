package info.bem.tools.bemplugin.cli;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by h4 on 12/12/15.
 */
public class BemBlockSettings {
    public String node;
    public String bemExecutablePath;
    public String cwd;
    public String directory;
    public String block;
    public String[] techs;
    public String elName;
    public String modName;

    public static BemBlockSettings build(@NotNull String cwd,
                                         @NotNull String directory,
                                         @NotNull String nodeInterpreter,
                                         @NotNull String bemExecutablePath,
                                         @NotNull String block
    ) {
        BemBlockSettings settings = new BemBlockSettings();
        settings.cwd = cwd;
        settings.directory = directory;
        settings.node = nodeInterpreter;
        settings.bemExecutablePath = bemExecutablePath;
        settings.block = block;
        return settings;
    }
}
