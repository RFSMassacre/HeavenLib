package us.rfsmassacre.heavenlib.spigot.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import us.rfsmassacre.heavenlib.spigot.managers.LocaleManager;

import java.util.ArrayList;

public abstract class Menu
{
    protected String title;
    protected int rows;
    protected ArrayList<Icon> icons;

    public Menu(String title, int rows)
    {
        this.title = LocaleManager.format(title);
        this.rows = rows;
        this.icons = new ArrayList<>();
    }

    public void addIcon(Icon icon)
    {
        this.icons.add(icon);
    }

    public String getTitle()
    {
        return title;
    }
    public int getRows()
    {
        return rows;
    }
    public ArrayList<Icon> getIcons()
    {
        return icons;
    }
    public boolean isTaken(int slot)
    {
        for (Icon icon : icons)
        {
            if (icon.getSlot() == slot)
            {
                return true;
            }
        }

        return false;
    }

    public Inventory createInventory(Player player)
    {
        //Retrieve the menu if it's the same inventory
        Inventory inventory = Bukkit.createInventory(null, rows * 9, title);

        //Add and place all of the items.
        updateIcons(player);
        for (Icon icon : icons)
        {
            inventory.setItem(icon.getSlot(), icon.getItemStack());
        }

        return inventory;
    }
    public void updateInventory(Player player)
    {
        String inventoryTitle = player.getOpenInventory().getTitle();
        if (inventoryTitle.equals(title))
        {
            Inventory inventory = player.getOpenInventory().getTopInventory();

            //Remove all icons first
            inventory.clear();
            icons.clear();

            //Add and place all of the items.
            updateIcons(player);
            for (Icon icon : icons)
            {
                inventory.setItem(icon.getSlot(), icon.getItemStack());
            }
        }
    }

    public boolean inInventory(Player player)
    {
        return player.getOpenInventory().getTitle().equals(title);
    }

    public abstract void updateIcons(Player player);
}
