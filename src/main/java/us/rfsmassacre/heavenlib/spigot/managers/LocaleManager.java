package us.rfsmassacre.heavenlib.spigot.managers;

/*
 * Manually saving the locale files this way makes it
 * consistent and simple to load from the default locale
 * saved in the jar in the event the user deleted the value
 * from the new config file.
 */

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class LocaleManager extends ResourceManager
{
    public LocaleManager(JavaPlugin instance)
    {
        super(instance, "locale.yml");
    }

    //LOCALE.YML
    //Gets file or default file when needed
    public String getMessage(String key)
    {
        String message = file.getString(key, defaultFile.getString(key));
        if (message == null)
        {
            message = "";
        }
        return format(message);
    }
    public List<String> getMessageList(String key)
    {
        List<String> stringList = file.getStringList(key);
        if (stringList.isEmpty())
            stringList = defaultFile.getStringList(key);

        return stringList;
    }

    /*
     * Replace the placer holder with its variable.
     */
    private String replaceHolders(String locale, String[] replacers)
    {
        String message = locale;

        for (int slot = 0; slot < replacers.length; slot += 2)
        {
            message = message.replace(replacers[slot], replacers[slot + 1]);
        }

        return message;
    }

    public void sendMessage(CommandSender sender, String message, String...replacers)
    {
        sender.sendMessage(format(replaceHolders(message, replacers)));
    }
    public void sendLocale(CommandSender sender, String key, String...replacers)
    {
        if (!getMessage(key).isEmpty())
        {
            sendMessage(sender, getMessage("prefix") + getMessage(key), replacers);
        }
    }

    public void broadcastMessage(String message, String...replacers)
    {
        Bukkit.broadcastMessage(format(replaceHolders(message, replacers)));
    }
    public void broadcastLocale(boolean prefix, String key, String...replacers)
    {
        Bukkit.broadcastMessage(format(replaceHolders(prefix ? getMessage("prefix") + getMessage(key) : "" + getMessage(key), replacers)));
    }
    public void broadcastLocale(String key, String...replacers)
    {
        broadcastLocale(true, key, replacers);
    }

    public void sendActionMessage(Player player, String message, String...replacers)
    {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(format(replaceHolders(message, replacers))));
    }
    public void sendActionLocale(Player player, String key, String...replacers)
    {
        sendActionMessage(player, getMessage("prefix") + getMessage(key), replacers);
    }

    public void sendComponentMessage(Player player, String command, String hover, String message, String...replacers)
    {
        TextComponent text = new TextComponent(TextComponent.fromLegacyText(format(replaceHolders(message, replacers))));

        if (command != null)
        {
            String clickCommand = stripColors(replaceHolders(command, replacers));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
        }
        if (hover != null)
        {
            BaseComponent[] hoverText = TextComponent.fromLegacyText(format(replaceHolders(hover, replacers)));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        }

        player.spigot().sendMessage(text);
    }
    public void sendComponentLocale(boolean prefix, Player player, String command, String hover, String key, String...replacers)
    {
        sendComponentMessage(player, command, hover, prefix ? getMessage("prefix") + getMessage(key)
                : getMessage(key), replacers);
    }
    public void sendComponentLocale(Player player, String command, String hover, String key, String...replacers)
    {
        sendComponentLocale(true, player, command, hover, key, replacers);
    }

    /*
     * Format Functions
     */
    public static String format(String string)
    {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    public static String stripColors(String string)
    {
        return ChatColor.stripColor(format(string));
    }
    public static String title(String string)
    {
        return WordUtils.capitalizeFully(string.toLowerCase().replace("_", " "));
    }
}
