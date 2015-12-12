package info.bem.tools.bemplugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by h4 on 12/12/15.
 */
public class BemPluginProjectComponent implements ProjectComponent {
    protected Project project;
    public String bemBlockExecutable;
    public String nodeInterpreter;
    public boolean treatAsWarnings;
    public boolean pluginEnabled;

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
//        public boolean isSettingsValid() {
//            if (!settings.getVersion().equals(settingValidVersion)) {
//                validateSettings();
//                settingValidVersion = settings.getVersion();
//            }
//            return settingValidStatus;
//        }
        return true;
    }
}
