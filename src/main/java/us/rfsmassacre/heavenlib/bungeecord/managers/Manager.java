package us.rfsmassacre.heavenlib.bungeecord.managers;

import net.md_5.bungee.api.plugin.Plugin;

/*
 * This is to be extended to another managers for easy
 * re-use. All managers save an instance of the plugin
 * it's being used for.
 */
public abstract class Manager
{
    protected Plugin instance;

    public Manager(Plugin instance)
    {
        this.instance = instance;
    }
}