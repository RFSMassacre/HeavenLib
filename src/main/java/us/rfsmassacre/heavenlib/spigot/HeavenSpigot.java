package us.rfsmassacre.heavenlib.spigot;


import org.bukkit.Bukkit;
import us.rfsmassacre.heavenlib.spigot.plugins.SpigotPlugin;

/*
 * Simply make this load into Spigot or BungeeCord in order to pull the library into
 * other HeavenPlugins.
 */
public final class HeavenSpigot extends SpigotPlugin
{
    @Override
    public void onPostEnable()
    {
        locale.sendLocale(Bukkit.getConsoleSender(), "loaded");
    }
}
