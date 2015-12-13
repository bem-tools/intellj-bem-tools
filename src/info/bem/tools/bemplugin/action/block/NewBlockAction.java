package info.bem.tools.bemplugin.action.block;

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.ui.DocumentAdapter;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import info.bem.tools.bemplugin.BemPluginProjectComponent;
import info.bem.tools.bemplugin.BemIcons;
import info.bem.tools.bemplugin.cli.BemBlockResult;
import info.bem.tools.bemplugin.cli.BemBlockRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.DocumentEvent;
import javax.swing.*;


public class NewBlockAction extends CreateElementActionBase implements DumbAware {

    public static boolean isBemPluginEnabled(Project project) {
        if (project != null) {
            BemPluginProjectComponent component = project.getComponent(BemPluginProjectComponent.class);
            return component.isEnabled();
        }
        return false;
    }

    @Override
    public void update(AnActionEvent e) {
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

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(Project project, PsiDirectory psiDirectory) {
        final NewBlockDialog dialog = new NewBlockDialog(project);
        dialog.show();
        return new PsiElement[0];
    }

    @NotNull
    @Override
    protected PsiElement[] create(String s, PsiDirectory psiDirectory) throws Exception {
        return new PsiElement[0];
    }

    @Override
    protected String getErrorTitle() {
        return null;
    }

    @Override
    protected String getCommandName() {
        return "BEM Mother fucka";
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, String s) {
        return null;
    }

    private class NewBlockDialog extends DialogWrapper {
        private JPanel newBlockTopPanel;
        private JTextField blockNameTextField;

        private final Project myProject;

        public NewBlockDialog(final Project project) {
            super(project, true);
            myProject = project;

            init();

            setTitle("Create New BEM Block");
            setOKActionEnabled(false);

            blockNameTextField.getDocument().addDocumentListener(new DocumentAdapter() {
                protected void textChanged(DocumentEvent e) {
                    setOKActionEnabled(!blockNameTextField.getText().isEmpty());
                }
            });
        }

        protected JComponent createCenterPanel() {
            return newBlockTopPanel;
        }

        public JComponent getPreferredFocusedComponent() {
            return blockNameTextField;
        }
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
