package us.rfsmassacre.heavenlib.spigot.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class GsonManager<T> extends Manager
{
    private File folder;
    private File childFolder;

    private final Class<T> clazz;

    public GsonManager(JavaPlugin plugin, String parent, String folderName, Class<T> clazz)
    {
        super(plugin);

        this.folder = new File(plugin.getDataFolder() + "/" + parent);
        folder.mkdir();

        this.childFolder = new File(plugin.getDataFolder() + "/" + parent + (folderName.isEmpty() ? "" : "/"
                + folderName));
        childFolder.mkdir();

        this.clazz = clazz;
    }

    /**
     * Read object from file.
     * @param fileName Name of file.
     * @return Object from file.
     */
    public T read(String fileName)
    {
        File file = getFile(fileName);

        try
        {
            if (file.exists())
            {
                BufferedReader reader = Files.newBufferedReader(file.toPath());
                return new Gson().fromJson(reader, clazz);
            }
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Write brand new blank file.
     * @param fileName Name of file.
     * @param overwrite Make new file over already existing file.
     */
    public void copy(String fileName, boolean overwrite)
    {
        InputStream stream = instance.getResource(fileName);
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(stream));
        Gson gson = new Gson();
        T t = gson.fromJson(reader, clazz);

        try
        {
            File file = getFile(fileName);
            if (overwrite)
            {
                file.delete();
            }

            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            gson.toJson(t, writer);
            writer.flush();
            writer.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();;
        }
    }

    public void write(String fileName, T t)
    {
        File file = getFile(fileName);

        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(t, writer);
            writer.flush();
            writer.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public void delete(String fileName)
    {
        File file = getFile(fileName);
        if (file.exists())
        {
            file.delete();
        }
    }

    public File getFile(String fileName)
    {
        return new File(childFolder.getPath() + "/" + fileName + (fileName.endsWith(".json") ? "" : ".json"));
    }

    public Set<T> all()
    {
        Set<T> all = new HashSet<>();

        try
        {
            for (File file : childFolder.listFiles())
            {
                all.add(read(file.getName()));
            }
        }
        catch (NullPointerException exception)
        {
            //Do nothing
        }

        return all;
    }
}
