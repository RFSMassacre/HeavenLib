package us.rfsmassacre.heavenlib.spigot.plugins;

import org.bukkit.plugin.java.JavaPlugin;
import us.rfsmassacre.heavenlib.spigot.managers.ConfigManager;
import us.rfsmassacre.heavenlib.spigot.managers.DependencyManager;
import us.rfsmassacre.heavenlib.spigot.managers.LocaleManager;

public abstract class SpigotPlugin extends JavaPlugin
{
    protected static SpigotPlugin instance;

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

    public static SpigotPlugin getInstance()
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
