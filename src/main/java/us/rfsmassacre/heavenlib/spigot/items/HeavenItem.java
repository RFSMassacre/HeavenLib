package us.rfsmassacre.heavenlib.spigot.items;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import us.rfsmassacre.heavenlib.spigot.managers.LocaleManager;
import us.rfsmassacre.heavenlib.spigot.plugins.SpigotPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class HeavenItem
{
    protected ItemStack item;
    protected Material material;
    protected String name;
    protected String displayName;
    protected NamespacedKey key;
    protected Recipe recipe;

    public HeavenItem(SpigotPlugin instance, Material material, int amount, String name,
                      String displayName, List<String> lore)
    {
        this.item = new ItemStack(material, amount);

        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.key = new NamespacedKey(instance, name);
        this.recipe = createRecipe();

        this.setDisplayName(displayName);
        this.setItemLore(lore);

        //NBT
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.addCompound("HeavenPlugin");
        nbtItem.getCompound("HeavenPlugin").setString("IID", this.name);
        nbtItem.applyNBT(item);
    }

    public boolean equals(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.getType().equals(Material.AIR))
        {
            return false;
        }

        NBTItem otherItem = new NBTItem(itemStack);
        NBTCompound compound = otherItem.getCompound("HeavenPlugin");
        if (compound == null)
        {
            //Bukkit.broadcastMessage("Is Null!");
            return false;
        }

        String value = compound.getString("IID");
        //Bukkit.broadcastMessage(value);
        return this.name.equals(value);
    }

    public void setDisplayName(String displayName)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(LocaleManager.format(displayName));
        item.setItemMeta(meta);
    }
    //Adds the ID to the first line to ensure when checking it's O(1).
    public void setItemLore(List<String> newLore)
    {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        List<String> lines = new ArrayList<>();
        for (String line : newLore)
        {
            lines.add(LocaleManager.format(line));
        }

        lore.addAll(lines);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    protected void addFlag(ItemFlag... flags)
    {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flags);
        item.setItemMeta(meta);
    }

    /*
     * Getters
     */
    public Material getType()
    {
        return material;
    }
    public String getName()
    {
        return name;
    }
    public String getDisplayName()
    {
        return displayName;
    }
    public Recipe getRecipe()
    {
        return recipe;
    }
    public ItemStack getItemStack()
    {
        return item;
    }

    /*
     * Recipe that is needed to craft item.
     */
    protected abstract Recipe createRecipe();
}
