package com.github.oxydrugplugs;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.plugin.java.JavaPlugin;


public class OxyClass extends JavaPlugin implements Listener {

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        HashMap<String, String> players = new HashMap<String, String>();
        this.getConfig().createSection("Classes", players);
        this.getConfig().getConfigurationSection("Classes").createSection("Feather", players);


        this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    private void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§4§lClass Selector");

        ItemStack noFallDamage = new ItemStack(Material.FEATHER);
        ItemMeta meta = noFallDamage.getItemMeta();

        meta.setDisplayName("§aFeather Feet");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§2Receive no fall damage");
        meta.setLore(lore);

        noFallDamage.setItemMeta(meta);

        inv.setItem(0, noFallDamage);

        player.openInventory(inv);
    }

    // ==============================================================================================================

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!ChatColor.stripColor(event.getInventory().getType().name()).equalsIgnoreCase("Class Selector")) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR
                || !event.getCurrentItem().hasItemMeta()) {
            player.closeInventory();
            return;
        }

        String uuid = player.getUniqueId().toString();

        if(this.getConfig().getConfigurationSection("Classes").contains(uuid)){
            player.sendMessage("§cYou have already chosen a class!");
            player.closeInventory();
            return;
        }

        switch (event.getCurrentItem().getType()) {
            case FEATHER:

                this.getConfig().getConfigurationSection("Feather").set("UUID", uuid);
                saveConfig();

                player.sendMessage("§2You chose the §a§oFeather Feet §2class!");
                player.sendMessage("§4§o§lNote: §c§oYou cannot change your class");

                player.closeInventory();
                return;

            default:
                break;
        }

    }
    // ==============================================================================================================

    @EventHandler
    public void onFall(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            String uuid = player.getUniqueId().toString();
            if(!this.getConfig().getConfigurationSection("Feather").contains(uuid)){
                return;
            }

            event.setCancelled(true);
            return;
        }
    }
    // ==============================================================================================================

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = event.getPlayer();
            String uuid = player.getUniqueId().toString();
            if(this.getConfig().getStringList("joined").contains(uuid)){
                return;
            }

            this.getConfig().getStringList("joined").add(uuid);
            ItemStack slimeball = new ItemStack(Material.SLIME_BALL);
            ItemMeta meta = slimeball.getItemMeta();
            meta.setDisplayName("§a§lClass Selector §7(Right Click)");

            slimeball.setItemMeta(meta);

            player.getInventory().addItem(slimeball);
        }
    }

    // ==============================================================================================================

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (action == Action.PHYSICAL || item == null || item.getType() == Material.AIR) {
            return;
        }

        if (item.getType() == Material.SLIME_BALL) {
            openGUI(event.getPlayer());
            return;
        }
    }
}

