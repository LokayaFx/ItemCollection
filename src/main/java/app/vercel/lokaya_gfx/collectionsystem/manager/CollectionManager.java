package app.vercel.lokaya_gfx.collectionsystem.manager;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CollectionManager {

    private final JavaPlugin plugin;
    
    // Milestones mapping: Level -> Amount of blocks required
    // A TreeMap is used because it intrinsically sorts by Level
    private final NavigableMap<Integer, Integer> levels = new TreeMap<>(Map.of(
            1, 100,
            2, 500,
            3, 1000,
            4, 2500,
            5, 5000
    ));

    public CollectionManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Constructs the custom NamespacedKey for a distinct material.
     * NBT Keys are required to be lowercase.
     */
    private NamespacedKey getKey(Material material) {
        return new NamespacedKey(plugin, "collection_" + material.name().toLowerCase());
    }

    /**
     * Adds progress to a player's collection locally via NBT data.
     *
     * @param player   The player to process
     * @param material The collected material
     * @param amount   The incremental factor (usually +1 from block breaks)
     */
    public void addProgress(Player player, Material material, int amount) {
        // Access the player's NBT PersistentDataContainer provided by Spigot API
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey key = getKey(material);

        int currentAmount = 0;

        // Retrieve the current integer value from NBT Custom Data if it already exists
        if (dataContainer.has(key, PersistentDataType.INTEGER)) {
            Integer stored = dataContainer.get(key, PersistentDataType.INTEGER);
            if (stored != null) {
                currentAmount = stored;
            }
        }

        int newAmount = currentAmount + amount;

        // Save the updated total value back into the Player's NBT
        dataContainer.set(key, PersistentDataType.INTEGER, newAmount);

        // Run the milestone logic against the new value
        checkLevelUp(player, material, newAmount);
    }

    /**
     * Core milestone logic: checks the new collection amount against predefined milestones.
     * Triggers a title update for the player upon exactly hitting a milestone boundary.
     *
     * @param player    The specific player
     * @param material  The material being assessed
     * @param newAmount The fresh count
     */
    public void checkLevelUp(Player player, Material material, int newAmount) {
        for (Map.Entry<Integer, Integer> entry : levels.entrySet()) {
            int level = entry.getKey();
            int requiredAmount = entry.getValue();

            // A strict match to ensure the title only sends exactly once per level up.
            if (newAmount == requiredAmount) {
                String friendlyMaterialName = formatMaterialName(material);
                
                // Sends a titled message: "Collection Unlocked: [Material] Level [X]"
                player.sendTitle(
                        "§6§lCollection Unlocked",
                        "§e" + friendlyMaterialName + " Level " + level,
                        10, 70, 20
                );
                player.sendMessage("§aYou have unlocked " + friendlyMaterialName + " Collection Level " + level + "!");
            }
        }
    }

    /**
     * Computes the visual string representation of a player's collection progress.
     * Designed to be integrated in GUIs or Scoreboard Placeholder systems.
     *
     * @param player   The specific player
     * @param material The collection block/material
     * @return Formatted string (e.g. "100 / 500", or "5,000 (Max)")
     */
    public String getCollectionProgress(Player player, Material material) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey key = getKey(material);

        // Fetch data defaulting to 0 if nothing exists yet
        int currentAmount = dataContainer.getOrDefault(key, PersistentDataType.INTEGER, 0);

        // Establish the upcoming milestone
        int nextLevelIndex = -1;
        int nextAmountNeeded = -1;
        
        for (Map.Entry<Integer, Integer> entry : levels.entrySet()) {
            if (currentAmount < entry.getValue()) {
                nextLevelIndex = entry.getKey();
                nextAmountNeeded = entry.getValue();
                break;
            }
        }

        // Returns maximum reached state format
        if (nextAmountNeeded == -1) {
            return String.format("%,d (Max Level)", currentAmount);
        }

        // Return standardized partial progress string
        return String.format("%,d / %,d (Lvl %d)", currentAmount, nextAmountNeeded, nextLevelIndex);
    }

    public String getProgressBar(Player player, Material material) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey key = getKey(material);

        int currentAmount = dataContainer.getOrDefault(key, PersistentDataType.INTEGER, 0);
        int nextAmountNeeded = -1;
        
        for (Map.Entry<Integer, Integer> entry : levels.entrySet()) {
            if (currentAmount < entry.getValue()) {
                nextAmountNeeded = entry.getValue();
                break;
            }
        }

        int totalBars = 20;
        if (nextAmountNeeded == -1) {
            return "§a" + "|".repeat(totalBars);
        }

        float percent = Math.min((float) currentAmount / nextAmountNeeded, 1.0f);
        int progressBars = (int) (totalBars * percent);
        int leftOver = totalBars - progressBars;

        return "§a" + "|".repeat(progressBars) + "§7" + "|".repeat(leftOver);
    }

    /**
     * Helper method to convert complex internal material enums to human readable forms.
     * (e.g. DEEPSLATE_DIAMOND_ORE -> Deepslate Diamond Ore)
     */
    private String formatMaterialName(Material material) {
        String[] words = material.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            sb.append(word.substring(0, 1).toUpperCase());
            sb.append(word.substring(1).toLowerCase());
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}
