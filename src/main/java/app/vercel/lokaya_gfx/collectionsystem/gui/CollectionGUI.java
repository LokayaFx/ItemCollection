package app.vercel.lokaya_gfx.collectionsystem.gui;

import app.vercel.lokaya_gfx.collectionsystem.manager.CollectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CollectionGUI implements InventoryHolder {
    private final Inventory inventory;
    private final CollectionManager manager;
    private final Player player;

    public CollectionGUI(CollectionManager manager, Player player) {
        this.manager = manager;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, "§8Collections");
        initializeItems();
    }

    private void initializeItems() {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }

        for (int i = 0; i < 54; i++) {
            // Border: row 0, row 5, col 0, col 8
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, filler);
            }
        }

        // Diamond in slot 22 (center of 54 slots)
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        ItemMeta diamondMeta = diamond.getItemMeta();
        if (diamondMeta != null) {
            diamondMeta.setDisplayName("§b§lDiamond Collection");
            List<String> lore = new ArrayList<>();
            lore.add("§7View your diamond collection progress.");
            lore.add("");
            lore.add("§fProgress: §e" + manager.getCollectionProgress(player, Material.DIAMOND_ORE));
            lore.add(manager.getProgressBar(player, Material.DIAMOND_ORE));
            diamondMeta.setLore(lore);
            diamond.setItemMeta(diamondMeta);
        }
        inventory.setItem(22, diamond);
    }

    public void open() {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
