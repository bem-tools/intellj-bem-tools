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

import java.util.concurrent.TimeUnit;

/**
 * Created by h4 on 12/12/15.
 */
public class BemBlockRunner {
    private BemBlockRunner() {
    }

    private static final Logger LOG = Logger.getInstance(BemBlockRunner.class);
    private static final int TIME_OUT = (int) TimeUnit.SECONDS.toMillis(120L);


    public static BemBlockResult run(@NotNull String cwd, @NotNull String nodeInterpreter, @NotNull String jscsBin, String directory) {
        BemBlockSettings settings = BemBlockSettings.build(cwd, nodeInterpreter, jscsBin, directory);
        return run(settings);
    }

    public static BemBlockResult run(@NotNull BemBlockSettings settings) {
        BemBlockResult result = new BemBlockResult();
        try {
            GeneralCommandLine commandLine = createCommandLineLint(settings);
            commandLine.addParameter("--fix");
            ProcessOutput out = execute(commandLine, TIME_OUT);
            result.errorOutput = out.getStderr();
            try {
                result.bemBlock = BemBlock.read(out.getStdout());
            } catch (Exception e) {
                LOG.error(e);
                //result.errorOutput = out.getStdout();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.errorOutput = e.toString();
        }
        return result;
    }

    private static GeneralCommandLine createCommandLineLint(BemBlockSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
        // TODO validate arguments (file exist etc)
//        commandLine.addParameter(settings.targetFile);
//        addParamIfExist(commandLine, "config", settings.config);
//        addParam(commandLine, "reporter", "checkstyle");
        commandLine.addParameter("-v");
//        addParamIfExist(commandLine, "preset", settings.preset);
//        addParamIfExist(commandLine, "esprima", settings.esprima);
//        if (settings.esnext) {
//            commandLine.addParameter("--esnext");
//        }
        return commandLine;
    }

    @NotNull
    private static GeneralCommandLine createCommandLine(@NotNull BemBlockSettings settings) {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(settings.cwd);
        if (SystemInfo.isWindows) {
            commandLine.setExePath(settings.bemBlockExecutablePath);
        } else {
            commandLine.setExePath(settings.node);
            commandLine.addParameter(settings.bemBlockExecutablePath);
            commandLine.addParameter(settings.directory);
        }
        return commandLine;
    }

    @NotNull
    private static ProcessOutput execute(@NotNull GeneralCommandLine commandLine, int timeoutInMilliseconds) throws ExecutionException {
        LOG.info("Running jscs command: " + commandLine.getCommandLineString());
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
