package info.bem.tools.bemplugin.cli;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.execution.process.*;
import info.bem.tools.bemplugin.cli.data.BemBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;


/**
 * Created by h4 on 12/12/15.
 */
public class BemBlockRunner {
    private BemBlockRunner() {
    }

    private static final Logger LOG = Logger.getInstance(BemBlockRunner.class);
    private static final int TIME_OUT = (int) TimeUnit.SECONDS.toMillis(120L);


    public static BemBlockResult run(@NotNull String cwd,
                                     @NotNull String directory,
                                     @NotNull String nodeInterpreter,
                                     @NotNull String bemBin,
                                     @NotNull String block
    ) {
        BemBlockSettings settings = BemBlockSettings.build(cwd, directory, nodeInterpreter, bemBin, block);

        return run(settings);
    }

    public static BemBlockResult run(@NotNull BemBlockSettings settings) {
        BemBlockResult result = new BemBlockResult();
        try {
            GeneralCommandLine commandLine = createCommandLineLint(settings);
            ProcessOutput out = execute(commandLine, TIME_OUT);
            result.errorOutput = out.getStderr();
            try {
                result.bemBlock = BemBlock.read(out.getStdout());
                VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
            } catch (Exception e) {
                LOG.error(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.errorOutput = e.toString();
        }
        return result;
    }

    private static GeneralCommandLine createCommandLineLint(BemBlockSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
        return commandLine;
    }

    @NotNull
    private static GeneralCommandLine createCommandLine(@NotNull BemBlockSettings settings) {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        // commandLine.setWorkDirectory(settings.cwd);
        commandLine.setWorkDirectory(settings.directory);

//        if (SystemInfo.isWindows) {}


        commandLine.setExePath(settings.node);

        commandLine.setExePath(settings.node);
        commandLine.addParameter(settings.bemExecutablePath);

        commandLine.addParameter("create");
        commandLine.addParameter(settings.block);

        // commandLine.addParameter("-e");

        // String nodeCode = "var create = require('/usr/local/lib/node_modules/bem-tools-create'); create('" + settings.block  +"');";

        // commandLine.addParameter(nodeCode);

        return commandLine;
    }

    @NotNull
    private static ProcessOutput execute(@NotNull GeneralCommandLine commandLine, int timeoutInMilliseconds) throws ExecutionException {
        Process process = commandLine.createProcess();
        OSProcessHandler processHandler = new ColoredProcessHandler(process, commandLine.getCommandLineString());
        final ProcessOutput output = new ProcessOutput();
        processHandler.addProcessListener(new ProcessAdapter() {
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                if (outputType.equals(ProcessOutputTypes.STDERR)) {
                    output.appendStderr(event.getText());
                } else if (!outputType.equals(ProcessOutputTypes.SYSTEM)) {
                    output.appendStdout(event.getText());
                }
            }
        });
        processHandler.startNotify();
        if (processHandler.waitFor(timeoutInMilliseconds)) {
            output.setExitCode(process.exitValue());
        } else {
            processHandler.destroyProcess();
            output.setTimeout();
        }
        if (output.isTimeout()) {
            throw new ExecutionException("Command '" + commandLine.getCommandLineString() + "' is timed out.");
        }
        return output;
    }

    @NotNull
    public static String version(@NotNull BemBlockSettings settings) throws ExecutionException {
//        if (!new File(settings.bemExecutablePath).exists()) {
//            LOG.warn("Calling version with invalid jscs exe " + settings.bemExecutablePath);
//            return "";
//        }
//        ProcessOutput out = runVersion(settings);
//        if (out.getExitCode() == 0) {
//            return out.getStdout().trim();
//        }
        return "";
    }

    @NotNull
    private static ProcessOutput runVersion(@NotNull BemBlockSettings settings) throws ExecutionException {
        GeneralCommandLine commandLine = createCommandLine(settings);
        commandLine.addParameter("--version");
        return execute(commandLine, TIME_OUT);

    }
}