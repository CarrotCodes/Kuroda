package engineer.carrot.warren.kuroda.listener;

import engineer.carrot.warren.kuroda.KurodaPlugin;
import hudson.init.InitMilestone;
import hudson.init.Initializer;

@SuppressWarnings("unused")
public class PluginsPreparedListener {

    @Initializer(after = InitMilestone.PLUGINS_PREPARED)
    public static void init() {
        KurodaPlugin.INSTANCE.init();
    }

}
