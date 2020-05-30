package us.rfsmassacre.heavenlib.bungeecord.managers;

import net.md_5.bungee.api.plugin.Plugin;

public class DependencyManager extends Manager
{
    public DependencyManager(Plugin instance)
    {
        super(instance);
    }

    public boolean hasPlugin(String pluginName)
    {
        //Easy way to check if needed plugin is enabled
        return instance.getProxy().getPluginManager().getPlugin(pluginName) != null;
    }
}
