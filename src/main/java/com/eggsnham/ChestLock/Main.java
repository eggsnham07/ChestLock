package com.eggsnham.ChestLock;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.eggsnham.DebugLevel;
import com.eggsnham.DebugLogger;
import com.eggsnham.ChestLock.Lib.FileConfig;
import com.eggsnham.ChestLock.Lib.System;

public class Main extends JavaPlugin
{
    FileConfig fileConfig = new FileConfig(this);
    System sys = new System(this, fileConfig);
    DebugLogger logger;
    Plugin plugin = this;
    public void onEnable()
    {
        if(!new File("plugins/DebugLib.jar").exists()) {
            try {
                //Download DebugLib.jar
                fileConfig.downloadLib(new URL("https://github.com/eggsns-plugins/BukkitDebugLibrary/releases/download/0.0.4-SNAPSHOT/DebugLib.jar"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().disablePlugin(plugin);
                }
            };
            task.runTaskLater(this, 1);

        }

        Server server = Bukkit.getServer();

        try {
            //Try to set the logger variable and go to catch block if fails
            logger = new DebugLogger(this);
        } catch(NoClassDefFoundError noClassDefFoundError) {
            //Let server know that plugin is not working yet and that it needs to be restarted
            getLogger().log(Level.WARNING, "DebugLib.jar either has not been downloaded or plugin has not been restarted!");
            getLogger().log(Level.WARNING, "| If you have already restarted the plugin contact me at eggsnham07@gmail.com");
            getLogger().log(Level.WARNING, "| Until it has been fixed this plugin will remain disabled.");
            getLogger().log(Level.WARNING, "|----------------------------------------------------------------------------");
        }

        try {
            configureFiles();
        } catch(IOException ex) {
            getLogger().log(Level.SEVERE, String.valueOf(ex));
        }

        if(sys.getHostname().equals("archlinux")) logger.log(DebugLevel.INFO, "Plugin started!");

        this.getCommand("create-locked").setExecutor(new CreateLocked());
        this.getCommand("create-locked").setTabCompleter(new CreateLockedTab());

        server.getPluginManager().registerEvents(new onChestOpen(this), this);
        server.getPluginManager().registerEvents(new onChestPlace(this), this);
        server.getPluginManager().registerEvents(new onPlayerJoin(this), this);
    }

    public void configureFiles() throws IOException
    {
        File file = new File(this.getDataFolder() + "/config.yml");
        File debugFile = new File(this.getDataFolder() + "/debug.log");
        File folder = new File(this.getDataFolder() + "/");

        if(!folder.exists()) {
            folder.mkdir();
        }

        if(!file.exists()) {
            file.createNewFile();
        }

        if(!debugFile.exists()) {
            debugFile.createNewFile();

            fileConfig.writeToFile(debugFile, "==============System Info==============\n    Hostname: "
                    + sys.getHostname());
            fileConfig.writeToFile(debugFile, "==================End==================");
        }

        if(sys.getHostname().equals("archlinux")) {
            fileConfig.writeToYml(file, "development.debug", true);
        } else {
            fileConfig.writeToYml(file, "development.debug", false);
        }
    }
}
