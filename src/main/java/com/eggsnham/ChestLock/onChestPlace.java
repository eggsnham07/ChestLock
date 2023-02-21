package com.eggsnham.ChestLock;

import com.eggsnham.ChestLock.Lib.FileConfig;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class onChestPlace implements Listener {
    Plugin plugin;
    FileConfig fileConfig;

    public onChestPlace(Plugin plugin)
    {
        this.plugin = plugin;
        this.fileConfig = new FileConfig(plugin);
    }

    @EventHandler
    public void onChestPlace(BlockPlaceEvent event)
    {
        double x = event.getBlock().getLocation().getX();
        double y = event.getBlock().getLocation().getY();
        double z = event.getBlock().getLocation().getZ();
        File file = new File(plugin.getDataFolder(), event.getPlayer().getName() + "/userdata.yml");
        if((event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.BARREL) &&
                event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Locked")) {
            List<String> chests = fileConfig.readFromYmlList(file, "chests");
            chests.add(x + ", " + y + ", " + z);

            fileConfig.writeToYml(file, "chests", chests);

            event.getPlayer().sendMessage("Created Locked Chest!");
        }
    }
}
