package info.bem.tools.bemplugin.settings;

import info.bem.tools.bemplugin.BemPluginProjectComponent;
import info.bem.tools.bemplugin.cli.BemBlockRunner;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.ExecutionException;
import com.wix.nodejs.NodeFinder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.NotNullProducer;
import com.intellij.util.ui.UIUtil;
import com.intellij.webcore.ui.SwingHelper;
import info.bem.tools.bemplugin.cli.BemBlockSettings;
import com.wix.settings.ValidationInfo;
import com.wix.settings.ValidationUtils;
import com.wix.ui.PackagesNotificationPanel;
import com.wix.utils.FileUtils;
import info.bem.tools.bemplugin.cli.BemFinder;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.util.NotNullProducer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BemSettingsPage implements Configurable {
    public static final String FIX_IT = "Fix it";
    public static final String HOW_TO_USE_BEM = "How to Use BEM";
    public static final String HOW_TO_USE_LINK = " https://github.com/bem-tools/intellj-bem-tools";
    protected Project project;

    private JPanel panel;
    private JPanel errorPanel;
    private TextFieldWithHistoryWithBrowseButton bemBinField;
    private TextFieldWithHistoryWithBrowseButton nodeInterpreterField;
    private TextFieldWithHistoryWithBrowseButton bemrcFile;
    private HyperlinkLabel usageLink;
    private JLabel nodeInterpreterLabel;
    private JLabel versionLabel;
    private JLabel pathToBemBinLabel;
    private JLabel bemConfigFilePathLabel;
    private final PackagesNotificationPanel packagesNotificationPanel;
    private JRadioButton searchForBemrcInRadioButton;
    private JRadioButton useProjectBemrcRadioButton;

    public BemSettingsPage(@NotNull final Project project) {
        this.project = project;

        configNodeField();
        configBinField();

        this.packagesNotificationPanel = new PackagesNotificationPanel(project);
        errorPanel.add(this.packagesNotificationPanel.getComponent(), BorderLayout.CENTER);

        DocumentAdapter docAdp = new DocumentAdapter() {
            protected void textChanged(DocumentEvent e) {
                updateLaterInEDT();
            }
        };

        nodeInterpreterField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
        bemBinField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
    }

    private void addDocumentListenerToComp(TextFieldWithHistoryWithBrowseButton field, DocumentAdapter docAdp) {
        field.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
    }

    private File getProjectPath() {
        if (project == null || project.getBaseDir() == null) {
            return null;
        }
        return new File(project.getBaseDir().getPath());
    }

    private void updateLaterInEDT() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                BemSettingsPage.this.update();
            }
        });
    }

    private void update() {
        ApplicationManager.getApplication().assertIsDispatchThread();
    }

    private void setEnabledState(boolean enabled) {
        nodeInterpreterField.setEnabled(enabled);
        nodeInterpreterLabel.setEnabled(enabled);
    }

    private void validateField(List<ValidationInfo> errors, TextFieldWithHistoryWithBrowseButton field, boolean allowEmpty, String message) {
        if (!ValidationUtils.validatePath(project, field.getChildComponent().getText(), allowEmpty)) {
            ValidationInfo error = new ValidationInfo(field.getChildComponent().getTextEditor(), message, FIX_IT);
            errors.add(error);
        }
    }

    private void validate() {
        List<ValidationInfo> errors = new ArrayList<ValidationInfo>();
        validateField(errors, bemBinField, false, "Path to bem is invalid {{LINK}}");
        validateField(errors, nodeInterpreterField, false, "Path to node interpreter is invalid {{LINK}}");
        if (errors.isEmpty()) {
            try {
                packagesNotificationPanel.removeAllLinkHandlers();
            } catch (Exception e) {
                e.printStackTrace();
            }
            packagesNotificationPanel.hide();
        } else {
            packagesNotificationPanel.showErrors(errors);
        }
    }

    private BemBlockSettings settings;

    private static TextFieldWithHistory configWithDefaults(TextFieldWithHistoryWithBrowseButton field) {
        TextFieldWithHistory textFieldWithHistory = field.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);
        return textFieldWithHistory;
    }

    private void configNodeField() {
        TextFieldWithHistory textFieldWithHistory = configWithDefaults(nodeInterpreterField);
        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
                List<File> newFiles = NodeFinder.searchAllScopesForBin(getProjectPath(), "node");
                return FileUtils.toAbsolutePath(newFiles);
            }
        });
        SwingHelper.installFileCompletionAndBrowseDialog(project, nodeInterpreterField, "Select Node interpreter", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "BEM";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadSettings();
        return panel;
    }

    private static boolean areEqual(TextFieldWithHistoryWithBrowseButton field, String value) {
        return field.getChildComponent().getText().equals(value);
    }

    @Override
    public boolean isModified() {
        Settings s = getSettings();
        Settings uiSettings = toSettings();
        return !s.isEqualTo(uiSettings);
    }

    @Override
    public void apply() throws ConfigurationException {
        saveSettings();
        PsiManager.getInstance(project).dropResolveCaches();
    }

    protected void saveSettings() {
        Settings settings = getSettings();
        copyTo(settings);
        project.getComponent(BemPluginProjectComponent.class).validateSettings();
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    public Settings toSettings() {
        Settings settings = new Settings();
        copyTo(settings);
        return settings;
    }

    public void copyTo(Settings settings) {
        settings.nodeInterpreter = nodeInterpreterField.getChildComponent().getText();
        settings.bemExecutable = bemBinField.getChildComponent().getText();
    }

    protected void loadSettings() {
        Settings settings = getSettings();
        nodeInterpreterField.getChildComponent().setText(settings.nodeInterpreter);
        bemBinField.getChildComponent().setText(settings.bemExecutable);
        setEnabledState(true);
    }

    @Override
    public void reset() {
        loadSettings();
    }

    @Override
    public void disposeUIResources() {
    }

    protected Settings getSettings() {
        return Settings.getInstance(project);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        usageLink = SwingHelper.createWebHyperlink(HOW_TO_USE_BEM, HOW_TO_USE_LINK);
    }

    private void getVersion() {
        if (settings != null &&
                areEqual(nodeInterpreterField, settings.node) &&
                areEqual(bemBinField, settings.bemExecutablePath) &&
                settings.cwd.equals(project.getBasePath())
                ) {
            return;
        }
        settings = new BemBlockSettings();
        settings.node = nodeInterpreterField.getChildComponent().getText();
        settings.bemExecutablePath = bemBinField.getChildComponent().getText();
        settings.cwd = project.getBasePath();
        try {
            String version = BemBlockRunner.version(settings);
            versionLabel.setText(version.trim());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void configBinField() {
        configWithDefaults(bemBinField);
        SwingHelper.addHistoryOnExpansion(bemBinField.getChildComponent(), new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
                List<File> newFiles = BemFinder.searchForBemBin(getProjectPath());
                return FileUtils.toAbsolutePath(newFiles);
            }
        });
        SwingHelper.installFileCompletionAndBrowseDialog(project, bemBinField, "Select bem.js cli", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }
}