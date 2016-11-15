package info.bem.tools.bemplugin;

import info.bem.tools.bemplugin.settings.Settings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import com.wix.utils.FileUtils;
import com.wix.utils.FileUtils.ValidationStatus;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

import javax.swing.event.HyperlinkEvent;

/**
 * Created by h4 on 12/12/15.
 */
public class BemPluginProjectComponent implements ProjectComponent {
    public static final String FIX_CONFIG_HREF = "\n<a href=\"#\">Fix Configuration</a>";
    public Settings settings;
    protected Project project;
    public String nodeInterpreter;
    public String bemExecutable;
    public boolean treatAsWarnings;
    public boolean pluginEnabled;
    private static final Logger LOG = Logger.getInstance(BemBundle.LOG_ID);
    protected boolean settingValidStatus;
    protected String settingValidVersion;
    protected String settingVersionLastShowNotification;

    public static final String PLUGIN_NAME = "BEM";

    public BemPluginProjectComponent(Project project) {
        this.project = project;
        settings = Settings.getInstance(project);
    }

    @Override
    public void projectOpened() {
        if (isEnabled()) {
            isSettingsValid();
        }
    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {
        if (isEnabled()) {
            isSettingsValid();
        }
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "BemPluginProjectComponent";
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isSettingsValid() {
        if (!settings.getVersion().equals(settingValidVersion)) {
            validateSettings();
            settingValidVersion = settings.getVersion();
        }
        return settingValidStatus;
    }

    public boolean validateSettings() {
        boolean status = validateField("Node Interpreter", settings.nodeInterpreter, true, false, true);
        if (!status) {
            return false;
        }

        nodeInterpreter = settings.nodeInterpreter;
        bemExecutable = settings.bemExecutable;
        treatAsWarnings = settings.treatAllIssuesAsWarnings;
        pluginEnabled = settings.pluginEnabled;
        settingValidStatus = true;
        return true;
    }

    private boolean validateField(String fieldName, String value, boolean shouldBeAbsolute, boolean allowEmpty, boolean isFile) {
        ValidationStatus r = FileUtils.validateProjectPath(shouldBeAbsolute ? null : project, value, allowEmpty, isFile);
        if (isFile) {
            if (r == ValidationStatus.NOT_A_FILE) {
                // String msg = BemBundle.message("bem.file.is.not.a.file", fieldName, value);
                String msg = "File is not a file " + fieldName + " " + value;
                validationFailed(msg);
                return false;
            }
        } else {
            if (r == ValidationStatus.NOT_A_DIRECTORY) {
                // String msg = BemBundle.message("bem.directory.is.not.a.dir", fieldName, value);
                String msg = "Directory is not a dir "  + fieldName + " " + value;
                validationFailed(msg);
                return false;
            }
        }
        if (r == ValidationStatus.DOES_NOT_EXIST) {
            // String msg = BemBundle.message("bem.file.does.not.exist", fieldName, value);
            String msg = "File Does Not Exist "  + fieldName + " " + value;
            validationFailed(msg);
            return false;
        }
        return true;
    }

    private void validationFailed(String msg) {
        NotificationListener notificationListener = new NotificationListener() {
            @Override
            public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {

            }
        };
        String errorMessage = msg + FIX_CONFIG_HREF;
        showInfoNotification(errorMessage, NotificationType.WARNING, notificationListener);
        LOG.debug(msg);
        settingValidStatus = false;
    }

    protected void showErrorConfigNotification(String content) {
        if (!settings.getVersion().equals(settingVersionLastShowNotification)) {
            settingVersionLastShowNotification = settings.getVersion();
            showInfoNotification(content, NotificationType.WARNING);
        }
    }

    public void showWarn(String content) {
        showInfoNotification(content, NotificationType.WARNING);
    }

    public void showInfoNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public void showInfoNotification(String content, NotificationType type, NotificationListener notificationListener) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type, notificationListener);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public static void showNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification);
    }

    public static void showError(String content) {
        showNotification(content, NotificationType.ERROR);
    }
}
