package info.bem.tools.bemplugin.cli;

import com.google.common.base.Charsets;
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
                                     @NotNull String block,
                                     @NotNull String[] techs,
                                     @NotNull String elName,
                                     @NotNull String modName
    ) {
        BemBlockSettings settings = BemBlockSettings.build(cwd, directory, block, techs, elName, modName);
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
        commandLine.setWorkDirectory(settings.cwd);

        LOG.info("test!");


//        if (SystemInfo.isWindows) {
//            commandLine.setExePath(settings.bemBlockExecutablePath);
//        } else {
            commandLine.setExePath("node");
            commandLine.addParameter("-e");
            String nodeCode = "var childProcess = require('child_process');" +
                    "var path = require('path');" +
                    "var fs = require('fs');" +
                    "var globalNodeModules = childProcess.execSync('npm root -g').toString().trim();" +
                    "var packageDir = path.join(globalNodeModules, 'bem-tools-create');" +
                    "var create = require(packageDir); create('" + settings.directory  + '/' + settings.block  +"')";
            commandLine.addParameter(nodeCode);
//        }

        return commandLine;
    }

    @NotNull
    private static ProcessOutput execute(@NotNull GeneralCommandLine commandLine, int timeoutInMilliseconds) throws ExecutionException {
        Process process = commandLine.createProcess();
        OSProcessHandler processHandler = new ColoredProcessHandler(process, commandLine.getCommandLineString(), Charsets.UTF_8);
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
}