package us.rfsmassacre.heavenlib.bungeecord;

import us.rfsmassacre.heavenlib.bungeecord.plugins.BungeePlugin;

public class HeavenBungee extends BungeePlugin
{
    @Override
    public void onPostEnable()
    {
        locale.sendLocale(instance.getProxy().getConsole(), "loaded");
    }
}
