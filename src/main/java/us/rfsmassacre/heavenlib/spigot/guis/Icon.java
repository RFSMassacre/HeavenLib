package us.rfsmassacre.heavenlib.spigot.guis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.rfsmassacre.heavenlib.spigot.managers.LocaleManager;

import java.util.List;

public abstract class Icon
{
    protected int x;
    protected int y;
    protected int amount;
    protected String displayName;
    protected Material material;
    protected List<String> lore;

    public Icon(int x, int y, int amount, Material material, String displayName, List<String> lore)
    {
        this.x = x;
        this.y = y;
        this.amount = amount;
        this.displayName = LocaleManager.format(displayName);
        this.material = material;
        this.lore = lore;
    }

    public int getSlot()
    {
        return (x + ((y - 1) * 9)) - 1;
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public int getAmount()
    {
        return amount;
    }
    public String getDisplayName()
    {
        return displayName;
    }
    public Material getMaterial()
    {
        return material;
    }
    public List<String> getLore()
    {
        return lore;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    public void setLore(List<String> lore)
    {
        this.lore = lore;
    }

    public ItemStack getItemStack()
    {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /*
     * Also define a function to each slot.
     */
    public abstract void onClick(Player clicker);
}
