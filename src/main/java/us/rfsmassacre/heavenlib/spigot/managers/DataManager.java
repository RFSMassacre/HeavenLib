package us.rfsmassacre.heavenlib.spigot.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/*
 * DATA MANAGERS HANDLE FILES SUCH AS PLAYER OR OBJECT SAVES.
 * To be extended so you can specify what data types you need.
 */
public abstract class DataManager<T> extends Manager
{
    protected File folder;

    public DataManager(JavaPlugin instance, String folderName)
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
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            storeData(t, data);
            data.save(file);
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
                YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
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
     * BREAK DOWN YOUR OBJECT INTO PRIMITIE DATA TYPES TO BE
     * STORED IN THE YML FILE.
     *
     * To be casted if loading from this.
     */
    protected abstract void storeData(T t, YamlConfiguration data) throws IOException;

    protected abstract T loadData(YamlConfiguration data) throws IOException;
}
