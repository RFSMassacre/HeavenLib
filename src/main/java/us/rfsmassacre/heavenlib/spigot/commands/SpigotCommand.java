package us.rfsmassacre.heavenlib.spigot.commands;

/*
 * SpigotCommand is structured to avoid using long
 * if-else chains and instead sets up a list of
 * sub-commands to cycle through when running.
 *
 * If the sub-command equals the argument it calls
 * for, then it runs the function to execute.
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import us.rfsmassacre.heavenlib.spigot.plugins.SpigotPlugin;
import us.rfsmassacre.heavenlib.spigot.managers.ConfigManager;
import us.rfsmassacre.heavenlib.spigot.managers.LocaleManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class SpigotCommand implements TabExecutor
{
    protected ConfigManager config;
    protected LocaleManager locale;
    protected String pluginName;
    protected String commandName;

    protected LinkedHashMap<String, SubCommand> subCommands;

    public SpigotCommand(SpigotPlugin instance, String commandName)
    {
        this.config = instance.getConfigManager();
        this.locale = instance.getLocaleManager();
        this.pluginName = instance.getName().toLowerCase();
        this.commandName = commandName;

        this.subCommands = new LinkedHashMap<>();
        //Remember to define the main command when extending
        //this class.
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (subCommands.isEmpty())
        {
            //All commands MUST have a main sub-command.
            return false;
        }
        else if (args.length == 0)
        {
            //If no arguments are given, always run the first sub-command.
            subCommands.values().iterator().next().execute(sender, args);
            return true;
        }
        else
        {
            //If arguments are given, cycle through the right one.
            //If none found, it'll give an error defined.
            String argument = args[0].toLowerCase();
            if (subCommands.containsKey(argument))
            {
                subCommands.get(argument).execute(sender, args);
                return true;
            }
        }

        onInvalidArgs(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            //All subcommand names should be showed when typing the command in.
            List<String> suggestions = new ArrayList<>();
            for (String subCommand : subCommands.keySet())
            {
                if (subCommand.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                {
                    suggestions.add(subCommand);
                }
            }
            return suggestions;
        }
        else if (args.length > 1)
        {
            //This section should show the argument of each subcommand
            List<String> suggestions = new ArrayList<>();
            for (SubCommand subCommand : subCommands.values())
            {
                if (subCommand.name.equalsIgnoreCase(args[0]))
                {
                    List<String> tab = subCommand.onTabComplete(args);
                    if (tab == null)
                    {
                        return null;
                    }

                    for (String suggestion : tab)
                    {
                        if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        {
                            suggestions.add(suggestion);
                        }
                    }
                }
            }
            return suggestions;
        }
        return null;
    }

    //Runs when the wrong arguments are given.
    protected void onInvalidArgs(CommandSender sender)
    {
        locale.sendLocale(sender, "invalid.invalid-args", "{command}", commandName);
    }

    //Runs when there is no permission.
    protected void onCommandFail(CommandSender sender)
    {
        locale.sendLocale(sender, "invalid.no-perm");
    }

    //Add or remove subcommands from the map
    protected void addSubCommand(SubCommand subCommand)
    {
        subCommands.put(subCommand.name, subCommand);
    }
    protected void removeSubCommand(SubCommand subCommand)
    {
        subCommands.remove(subCommand.name);
    }

    /*
     * SubCommand
     */
    protected abstract class SubCommand
    {
        protected String name;
        protected String permission;

        public SubCommand(String name)
        {
            //Ensures that permissions are set properly
            this.name = name.toLowerCase();
            this.permission = pluginName + "." + commandName;
            if (!name.isEmpty())
            {
                this.permission = this.permission + "." + name;
            }
        }

        public boolean isConsole(CommandSender sender)
        {
            return !(sender instanceof Player);
        }

        public void execute(CommandSender sender, String[] args)
        {
            if (isConsole(sender))
            {
                onConsoleRun(sender, args);
            }
            else
            {
                if (sender.hasPermission(this.permission))
                {
                    onPlayerRun((Player)sender, args);
                }
                else
                {
                    onCommandFail(sender);
                }
            }

        }

        /*
         * Define what to run when player has permission.
         */
        protected abstract void onConsoleRun(CommandSender sender, String[] args);
        protected abstract void onPlayerRun(Player player, String[] args);
        protected abstract List<String> onTabComplete(String[] args);
    }
}
