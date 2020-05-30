package us.rfsmassacre.heavenlib.bungeecord.managers;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/*
 * DATA MANAGERS HANDLE FILES SUCH AS PLAYER OR OBJECT SAVES.
 * To be extended so you can specify what data types you need.
 */
public abstract class DataManager<T> extends Manager
{
    protected File folder;

    public DataManager(Plugin instance, String folderName)
    {
        super(instance);
        this.folder = new File(instance.getDataFolder() + "/" + folderName);

        //Create folder if not found
        if (!folder.exists())
            folder.mkdir();
    }

    protected File getFile(String fileName)
    {
        return new File(folder, (fileName.endsWith(".yml") ? fileName : fileName + ".yml"));
    }
    protected boolean createFile(String fileName)
    {
        File file = getFile(fileName);

        try
        {
            return file.createNewFile();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return false;
    }

    public void saveToFile(T t, String fileName)
    {
        //Delete and create a new file to save data.
        //Avoids failure to remove previous unwanted data.
        File file = getFile(fileName);
        deleteFile(fileName);
        createFile(fileName);

        try
        {
            Configuration data = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
            storeData(t, data);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(data, file);
        }
        catch (IOException exception)
        {
            //Print error on console
            exception.printStackTrace();
        }
    }

    public T loadFromFile(File file)
    {
        if (file.exists())
        {
            try
            {
                Configuration data = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
                return loadData(data);
            }
            catch (IOException | NullPointerException exception)
            {
                //Print error on console
                exception.printStackTrace();
            }
        }

        return null;
    }
    public T loadFromFile(String fileName)
    {
        return loadFromFile(getFile(fileName));
    }

    public void deleteFile(String fileName)
    {
        File file = getFile(fileName);
        if (file.exists())
            file.delete();
    }

    public File[] listFiles()
    {
        return folder.listFiles();
    }

    /*
     * I learned how to use generics since I made HeavenChat. This should be much easier.
     */
    protected abstract void storeData(T t, Configuration data) throws IOException;

    protected abstract T loadData(Configuration data) throws IOException;
}
