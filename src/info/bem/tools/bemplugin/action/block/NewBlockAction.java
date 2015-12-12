package info.bem.tools.bemplugin.action.block;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import info.bem.tools.bemplugin.BemPluginProjectComponent;
import info.bem.tools.bemplugin.BemIcons;
import info.bem.tools.bemplugin.cli.BemBlockResult;
import info.bem.tools.bemplugin.cli.BemBlockRunner;
import org.jetbrains.annotations.NotNull;


public class NewBlockAction extends DumbAwareAction {
    public static boolean isBemPluginEnabled(Project project) {
        if (project != null) {
            BemPluginProjectComponent component = project.getComponent(BemPluginProjectComponent.class);
            return component.isEnabled();
        }
        return false;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        BemPluginProjectComponent component = project.getComponent(BemPluginProjectComponent.class);
        component.nodeInterpreter = "/usr/local/bin/node";
        component.bemBlockExecutable = "/Users/h4/bemBlock.js";

//        BemBlockResult result = BemBlockRunner.run(project.getBasePath(), component.nodeInterpreter, component.bemBlockExecutable);
        BemBlockResult result = BemBlockRunner.run(project.getBasePath(), "/usr/local/bin/node", "/Users/h4/bemBlock.js");
    }

    public NewBlockAction() {
        super("BEM Block", "Create BEM blocks", BemIcons.BEM);
    }

    protected void write(@NotNull final Project project, @NotNull String blockName) {
        final RunResult execute = new WriteCommandAction(project) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {

            }

            public String getGroupID() {
                return "Create BEM Block";
            }
        }.execute();
    }
}
