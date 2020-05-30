package us.rfsmassacre.heavenlib.spigot.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class TextManager extends Manager
{
    private HashMap<String, List<String>> textCache;

    public TextManager(JavaPlugin instance, String folderName)
    {
        super(instance);
        this.textCache = new HashMap<>();

        //Create folder if needed
        File folder = new File(instance.getDataFolder() + "/" + folderName);
        if (!folder.exists())
        {
            folder.mkdir();
        }
    }

    public List<String> loadTextFile(String fileDirectory)
    {
        if (!textCache.containsKey(fileDirectory))
        {
            cacheTextFile(fileDirectory);
        }

        return textCache.get(fileDirectory);
    }
    public void clearCacheFiles()
    {
        textCache.clear();
    }
    public void cacheTextFile(String fileDirectory)
    {
        try
        {
            File file = new File(instance.getDataFolder() + "/" + fileDirectory);
            if (!file.exists())
            {
                file.createNewFile();
                instance.saveResource(fileDirectory, true);
            }
            textCache.put(fileDirectory, Files.readAllLines(file.toPath()));
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }
}
