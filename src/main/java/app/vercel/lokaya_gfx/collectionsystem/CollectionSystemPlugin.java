package app.vercel.lokaya_gfx.collectionsystem;

import app.vercel.lokaya_gfx.collectionsystem.command.CollectionCommand;
import app.vercel.lokaya_gfx.collectionsystem.listener.CollectionListener;
import app.vercel.lokaya_gfx.collectionsystem.manager.CollectionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CollectionSystemPlugin extends JavaPlugin {

    private CollectionManager collectionManager;

    @Override
    public void onEnable() {
        // Initialize the CollectionManager handling the PersistentDataContainer logic
        this.collectionManager = new CollectionManager(this);

        // Register the BlockBreakEvent listener for our core collection features
        getServer().getPluginManager().registerEvents(new CollectionListener(this.collectionManager), this);

        // Register the /collection command
        CollectionCommand collectionCmd = new CollectionCommand(this.collectionManager);
        getCommand("collection").setExecutor(collectionCmd);
        getCommand("collection").setTabCompleter(collectionCmd);

        getLogger().info("ItemCollection has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemCollection has been disabled.");
    }

    /**
     * Expose the manager so other plugins or systems can hook into it.
     */
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }
}
