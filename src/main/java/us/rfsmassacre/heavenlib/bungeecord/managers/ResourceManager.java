package us.rfsmassacre.heavenlib.bungeecord.managers;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/*
 * RESOURCE MANAGERS HANDLE FILES SUCH AS CONFIGS AND LOCALES.
 * To be extended so you can specify what data types you need.
 */
public abstract class ResourceManager extends Manager
{
    protected String fileName;
    protected Configuration file;
    protected Configuration defaultFile;

    public ResourceManager(Plugin instance, String fileName)
    {
        super(instance);
        this.fileName = fileName;

        reloadFiles();
    }

    public void reloadFiles()
    {
        File newFile = new File(instance.getDataFolder(), fileName);

        try
        {
            defaultFile = ConfigurationProvider.getProvider(YamlConfiguration.class).load(instance.getResourceAsStream(fileName));

            if (!newFile.exists())
            {
                newFile.createNewFile();
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(defaultFile, newFile);
            }

            file = ConfigurationProvider.getProvider(YamlConfiguration.class).load(newFile);
        }
        catch (IOException exception)
        {
            //Print error on console neatly
            exception.printStackTrace();
        }
    }
}
