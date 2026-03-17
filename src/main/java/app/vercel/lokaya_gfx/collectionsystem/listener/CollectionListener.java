package app.vercel.lokaya_gfx.collectionsystem.listener;

import app.vercel.lokaya_gfx.collectionsystem.manager.CollectionManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.EnumSet;

public class CollectionListener implements Listener {

    private final CollectionManager collectionManager;
    
    // Defines the 'Collection List'
    // EnumSet is chosen internally because it is highly performant for fixed enums compared to HashSet.
    private final EnumSet<Material> collectionList = EnumSet.of(
            Material.COAL_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.LAPIS_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_EMERALD_ORE
    );

    public CollectionListener(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Using MONITOR priority and ignoreCancelled ensures we strictly track successful block breaks,
     * skipping cases where things like block protection plugins cancelled the event prior.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Creative Mode verification.
        // Prevents users from gathering collection progress unfairly.
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Material blockType = event.getBlock().getType();

        // Verifies against the predefined 'Collection List' mapping
        if (collectionList.contains(blockType)) {
            // Fires up an increment for the NBT key of the distinct block
            collectionManager.addProgress(player, blockType, 1);
        }
    }

    /**
     * Prevent players from moving or taking items out of the Collection GUI.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof app.vercel.lokaya_gfx.collectionsystem.gui.CollectionGUI) {
            event.setCancelled(true);
        }
    }
}
