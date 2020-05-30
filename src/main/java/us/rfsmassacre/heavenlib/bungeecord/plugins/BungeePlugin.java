package us.rfsmassacre.heavenlib.bungeecord.plugins;

import net.md_5.bungee.api.plugin.Plugin;
import us.rfsmassacre.heavenlib.bungeecord.managers.ConfigManager;
import us.rfsmassacre.heavenlib.bungeecord.managers.DependencyManager;
import us.rfsmassacre.heavenlib.bungeecord.managers.LocaleManager;

public abstract class BungeePlugin extends Plugin
{
    protected static Plugin instance;

    protected ConfigManager config;
    protected LocaleManager locale;
    protected DependencyManager dependency;

    //Loading in order
    @Override
    public void onEnable()
    {
        //Create data folder
        if (!getDataFolder().exists())
        {
            getDataFolder().mkdir();
        }

        instance = this;

        //Load managers
        this.config = new ConfigManager(this);
        this.locale = new LocaleManager(this);
        this.dependency = new DependencyManager(this);

        //Run inherited functions
        onPostEnable();
    }

    //Override this so you load everything else after.
    public abstract void onPostEnable();

    public static Plugin getInstance()
    {
        return instance;
    }

    public ConfigManager getConfigManager()
    {
        return config;
    }
    public LocaleManager getLocaleManager()
    {
        return locale;
    }
    public DependencyManager getDependencyManager()
    {
        return dependency;
    }
}
