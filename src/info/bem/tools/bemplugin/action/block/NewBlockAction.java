package info.bem.tools.bemplugin.action.block;

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.sun.org.apache.xpath.internal.operations.Bool;
import info.bem.tools.bemplugin.BemPluginProjectComponent;
import info.bem.tools.bemplugin.BemIcons;
import info.bem.tools.bemplugin.cli.BemBlockResult;
import info.bem.tools.bemplugin.cli.BemBlockRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.DocumentEvent;
import javax.swing.*;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewBlockAction extends CreateElementActionBase implements DumbAware {
    private static final Logger LOG = Logger.getInstance("#info.bem.tools.bemplugin.action.block.NewBlockAction");
    protected Project project;
    protected NewBlockDialog dialog;

    public static boolean isBemPluginEnabled(Project project) {
        if (project != null) {
            BemPluginProjectComponent component = project.getComponent(BemPluginProjectComponent.class);
            return component.isEnabled();
        }
        return false;
    }

    @Override
    public void update(AnActionEvent e) {
        project = e.getProject();
        if (project == null) {
            return;
        }

        BemPluginProjectComponent component = project.getComponent(BemPluginProjectComponent.class);
        component.nodeInterpreter = "/usr/local/bin/node";
        component.bemBlockExecutable = "/Users/h4/bemBlock.js";

//        BemBlockResult result = BemBlockRunner.run(project.getBasePath(), component.nodeInterpreter, component.bemBlockExecutable);
//        BemBlockResult result = BemBlockRunner.run(project.getBasePath(), "/usr/local/bin/node", "/Users/h4/bemBlock.js");
    }

    public NewBlockAction() {
        super("BEM Block", "Create BEM blocks", BemIcons.BEM);

        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                // to prevent deadlocks, this code must run while not holding the ActionManager lock
//                FileTemplateManager manager = FileTemplateManager.getDefaultInstance();
//                final FileTemplate template = manager.getTemplate("RTFile3");
//                noinspection HardCodedStringLiteral
//                if (template != null && template.getExtension().equals("rt")) {
//                    manager.removeTemplate(template);
//                }
            }
        });
    }

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(Project project, PsiDirectory directory) {
        final MyInputValidator validator = new MyInputValidator(project, directory);
        dialog = new NewBlockDialog(project, validator);
        dialog.show();
        return validator.getCreatedElements();
    }

    @NotNull
    @Override
    protected PsiElement[] create(String newName, PsiDirectory psiDirectory) throws Exception {
        PsiElement createdFile;

        String directory = psiDirectory.toString().replace("PsiDirectory:","");

        final String[] techs = {};

        BemPluginProjectComponent component = project.getComponent(BemPluginProjectComponent.class);

        final String elName = "";
        final String modName = "";

        // Запускаем node-файл и он пыщь-пыщь что-то там делает
        // Потом всё нужно получить пути до новых файлов и вернуть список инстансов PsiFile
        BemBlockResult result = BemBlockRunner.run(project.getBasePath(), directory, newName, techs, elName, modName);
        return new PsiElement[0];
    }

    @Override
    protected String getErrorTitle() {
        return null;
    }

    @Override
    protected String getCommandName() {
        return "Create BEM Block Command";
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, String s) {
        return "Create BEM Block Action";
    }

    private class NewBlockDialog extends DialogWrapper {
        private JPanel newBlockTopPanel;
        private JTextField blockNameTextField;
        private JCheckBox depsCheckBox;
        private JCheckBox cssCheckBox;
        private JCheckBox bemhtmlCheckBox;
        private JCheckBox jsCheckBox;
        private final MyInputValidator myValidator;

        private final Project myProject;

        public NewBlockDialog(final Project project, final MyInputValidator validator) {
            super(project, true);
            myProject = project;
            myValidator = validator;

            init();

            setTitle("Create New BEM");
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

        protected void doOKAction() {
            final String inputString = blockNameTextField.getText().trim();

            if (myValidator.checkInput(inputString) && myValidator.canClose(inputString)) {
                close(OK_EXIT_CODE);
            }
            close(OK_EXIT_CODE);
        }
    }

    protected void write(@NotNull final Project project, @NotNull String blockName) {
        final RunResult execute = new WriteCommandAction(project) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {

            }

            public String getGroupID() {
                return "BEM Create";
            }
        }.execute();
    }
}
