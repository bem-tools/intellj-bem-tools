package info.bem.tools.bemplugin.cli;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by h4 on 12/12/15.
 */
public class BemBlockSettings {
    public String node;
    public String cwd;
    public String directory;
    public String block;
    public String[] techs;

    public static BemBlockSettings build(@NotNull String cwd,
                                         @NotNull String directory,
                                         String block,
                                         @NotNull String[] techs
    ) {
        BemBlockSettings settings = new BemBlockSettings();
        settings.cwd = cwd;
        settings.directory = directory;
        settings.block = block;
        settings.techs = techs;
        return settings;
    }
}
