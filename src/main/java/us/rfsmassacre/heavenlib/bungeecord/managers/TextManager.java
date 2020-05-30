package us.rfsmassacre.heavenlib.bungeecord.managers;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextManager extends Manager
{
    private HashMap<String, List<String>> textCache;

    public TextManager(Plugin instance, String folderName)
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
            InputStream is = instance.getResourceAsStream(fileDirectory);
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(is));
            ArrayList<String> lines = new ArrayList<String>();
            String line;
            while ((line = bfReader.readLine()) != null)
            {
                lines.add(line);
            }

            is.close();
            bfReader.close();
            textCache.put(fileDirectory, lines);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }
}