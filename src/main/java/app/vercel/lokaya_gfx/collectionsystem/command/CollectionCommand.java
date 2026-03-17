package app.vercel.lokaya_gfx.collectionsystem.command;

import app.vercel.lokaya_gfx.collectionsystem.gui.CollectionGUI;
import app.vercel.lokaya_gfx.collectionsystem.manager.CollectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionCommand implements CommandExecutor, TabCompleter {

    private final CollectionManager manager;
    private final List<String> validMaterials;

    public CollectionCommand(CollectionManager manager) {
        this.manager = manager;
        // Cache valid materials for quick lookup and tab completion
        this.validMaterials = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .map(m -> m.name().toLowerCase())
                .collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /collection <view|stats|add|reset>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "view":
                return handleView(sender);
            case "stats":
                return handleStats(sender, args);
            case "add":
                return handleAdd(sender, args);
            case "reset":
                return handleReset(sender, args);
            default:
                sender.sendMessage("§cUnknown subcommand. Usage: /collection <view|stats|add|reset>");
                return true;
        }
    }

    private boolean handleView(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can open the GUI.");
            return true;
        }
        CollectionGUI gui = new CollectionGUI(manager, player);
        gui.open();
        return true;
    }

    private boolean handleStats(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can view their stats.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /collection stats <material>");
            return true;
        }

        Material material = Material.matchMaterial(args[1].toUpperCase());
        if (material == null || !material.isItem()) {
            sender.sendMessage("§cInvalid Material.");
            return true;
        }

        String progress = manager.getCollectionProgress(player, material);
        sender.sendMessage("§aYour " + material.name() + " progress: §e" + progress);
        return true;
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("collection.admin")) {
            sender.sendMessage("§cNo Permission.");
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage("§cUsage: /collection add <player> <material> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        Material material = Material.matchMaterial(args[2].toUpperCase());
        if (material == null || !material.isItem()) {
            sender.sendMessage("§cInvalid Material.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount. Must be a number.");
            return true;
        }

        manager.addProgress(target, material, amount);
        sender.sendMessage("§aAdded " + amount + " to " + target.getName() + "'s " + material.name() + " collection.");
        return true;
    }

    private boolean handleReset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("collection.admin")) {
            sender.sendMessage("§cNo Permission.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /collection reset <player> <material>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        Material material = Material.matchMaterial(args[2].toUpperCase());
        if (material == null || !material.isItem()) {
            sender.sendMessage("§cInvalid Material.");
            return true;
        }

        // We reset by removing the PDC key natively using Bukkit API
        PersistentDataContainer dataContainer = target.getPersistentDataContainer();
        
        // This relies on the internal NamespacedKey structure we've set in CollectionManager
        // To keep this clean without altering other parts heavily, we can replicate the key generation:
        NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("ItemCollection"), 
                                              "collection_" + material.name().toLowerCase());
        
        dataContainer.remove(key);
        
        sender.sendMessage("§aSuccessfully reset " + target.getName() + "'s " + material.name() + " collection.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("view", "stats"));
            if (sender.hasPermission("collection.admin")) {
                subCommands.add("add");
                subCommands.add("reset");
            }
            for (String sub : subCommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("stats")) {
                for (String mat : validMaterials) {
                    if (mat.startsWith(args[1].toLowerCase())) {
                        completions.add(mat);
                    }
                }
            } else if ((subCommand.equals("add") || subCommand.equals("reset")) && sender.hasPermission("collection.admin")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(p.getName());
                    }
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if ((subCommand.equals("add") || subCommand.equals("reset")) && sender.hasPermission("collection.admin")) {
                for (String mat : validMaterials) {
                    if (mat.startsWith(args[2].toLowerCase())) {
                        completions.add(mat);
                    }
                }
            }
        }

        return completions;
    }
}
