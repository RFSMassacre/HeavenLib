package us.rfsmassacre.heavenlib.spigot.items;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import us.rfsmassacre.heavenlib.spigot.plugins.SpigotPlugin;
import us.rfsmassacre.heavenlib.spigot.managers.LocaleManager;

import java.util.ArrayList;
import java.util.List;

/*
 * Decided to stay consistent and make it so SpigotItems require
 * the instance of SpigotPlugin. Just didn't feel like the proper
 * etiquette.
 */
public abstract class SpigotItem extends ItemStack
{
    protected String name;
    protected String id;
    protected NamespacedKey key;
    protected Recipe recipe;
    protected NBTItem nbtItem;

    public SpigotItem(SpigotPlugin instance, String name, String displayName, ArrayList<String> lore, String id, Material material)
    {
        super(material);

        this.name = name;
        this.id = id;
        this.key = new NamespacedKey(instance, name);
        this.recipe = createRecipe();

        //Set the new data onto the itemstack
        setDisplayName(displayName);
        setItemLore(lore);

        //Ensures that the item is constructed before making a new recipe.
        this.recipe = createRecipe();

        this.nbtItem = new NBTItem(this);
        this.nbtItem.addCompound("Werewolves");
        this.nbtItem.setString("ID", name);
    }

    public String getItemName()
    {
        return name;
    }
    public String getDisplayName()
    {
        return getItemMeta().getDisplayName();
    }
    public List<String> getItemLore()
    {
        return getItemMeta().getLore();
    }
    public String getItemID()
    {
        return id;
    }
    public Recipe getRecipe()
    {
        return recipe;
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof ItemStack))
        {
            return false;
        }

        ItemStack item = (ItemStack)object;
        NBTItem other = new NBTItem(item);
        NBTCompound compound = other.getCompound("Werewolves");
        if (compound == null)
        {
            return false;
        }

        String value = compound.getString("ID");
        if (value.equals(this.name))
        {
            return true;
        }

        return false;
    }

    public void setDisplayName(String displayName)
    {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(LocaleManager.format(displayName));
        setItemMeta(meta);
    }
    //Adds the ID to the first line to ensure when checking it's O(1).
    public void setItemLore(ArrayList<String> newLore)
    {
        ItemMeta meta = getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.add(id);
        lore.addAll(newLore);
        meta.setLore(lore);
        setItemMeta(meta);
    }

    /*
     * All SpigotItems have recipes. Return null if not.
     */
    protected abstract Recipe createRecipe();
}
