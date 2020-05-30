package us.rfsmassacre.heavenlib.bungeecord.managers;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.List;

public class ConfigManager extends ResourceManager
{
    /*
     * Manually saving the config files this way makes it
     * consistent and simple to load from the default config
     * saved in the jar in the event the user deleted the value
     * from the new config file.
     */
    public ConfigManager(Plugin instance)
    {
        super(instance, "config.yml");
    }

    //CONFIG.YML
    //Gets config data or default config data when needed
    public String getString(String key)
    {
        return file.getString(key, defaultFile.getString(key));
    }
    public int getInt(String key)
    {
        return file.getInt(key, defaultFile.getInt(key));
    }
    public boolean getBoolean(String key)
    {
        return file.getBoolean(key, defaultFile.getBoolean(key));
    }
    public double getDouble(String key)
    {
        return file.getDouble(key, defaultFile.getDouble(key));
    }
    public long getLong(String key)
    {
        return file.getLong(key, defaultFile.getLong(key));
    }

    /*
     * For some reason the getList functions do not allow a
     * default parameter, so I just checked for null as a
     * backup check. It will return null if absolutely nothing
     * is found.
     */
    public List<String> getStringList(String key)
    {
        List<String> option = file.getStringList(key);
        if (option == null)
            option = defaultFile.getStringList(key);

        return option;
    }
    public List<Integer> getIntegerList(String key)
    {
        List<Integer> option = file.getIntList(key);
        if (option == null)
            option = defaultFile.getIntList(key);

        return option;
    }
    public List<Double> getDoubleList(String key)
    {
        List<Double> option = file.getDoubleList(key);
        if (option == null)
            option = defaultFile.getDoubleList(key);

        return option;
    }
    public List<Long> getLongList(String key)
    {
        List<Long> option = file.getLongList(key);
        if (option == null)
            option = defaultFile.getLongList(key);

        return option;
    }

    /*
     * This should return a map of key sections needed.
     */
    public Configuration getSection(String path)
    {
        return file.getSection(path) != null ? file.getSection(path) : defaultFile.getSection(path);
    }
}
