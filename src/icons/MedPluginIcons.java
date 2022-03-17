package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @autho zhangquan
 */
public interface MedPluginIcons {
    Icon ICON = IconLoader.getIcon("/META-INF/icon.png", MedPluginIcons.class);
    Icon ICON_CONFIG_LIB = IconLoader.getIcon("/META-INF/icon_config_lib.png", MedPluginIcons.class);
    Icon ICON_SOURCE_CONFIG = IconLoader.getIcon("/META-INF/icon_source_config.png", MedPluginIcons.class);
    Icon ICON_SHOW_DIFF = IconLoader.getIcon("/META-INF/icon_show_diff.png", MedPluginIcons.class);

}
