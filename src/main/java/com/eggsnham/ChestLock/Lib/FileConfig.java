package com.eggsnham.ChestLock.Lib;

import com.eggsnham.ChestLock.Exceptions.IncorrectFileType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.io.FilenameUtils;

public class FileConfig {
    Plugin plugin;
    public FileConfig(Plugin plugin) {
        this.plugin = plugin;
    }

    public void writeToFile(File file, String string)
    {
        int i;

        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            char ch[] = string.toCharArray();
            //Writing content on RAM
            for(i=0;i<string.length();i++) {
                fos.write(ch[i]);
            }
            //Saving content from RAM to hard drive
            fos.write('\n');
            fos.close();
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, String.valueOf(ex));
        }
    }

    public String readFromFile(File file)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String returnedString = "";
            String str;

            while((str = br.readLine()) != null) {
                returnedString += str;
            }
            br.close();
            return returnedString;
        } catch(Exception ex) {
            plugin.getLogger().log(Level.SEVERE, String.valueOf(ex));
            return "error";
        }
    }

    // String overload
    public boolean writeToYml(File file, String path, String content)
    {
        if(!file.getName().endsWith(".yml")) return false;
        FileConfiguration f = YamlConfiguration.loadConfiguration(file);
        f.set(path, content);
        try {
            f.save(file);
            return true;
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, String.valueOf(ex));
            return false;
        }
    }

    // Boolean overload
    public boolean writeToYml(File file, String path, boolean bool)
    {
        if(!file.getName().endsWith(".yml")) return false;
        FileConfiguration f = YamlConfiguration.loadConfiguration(file);
        f.set(path, bool);
        try {
            f.save(file);
            return true;
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, String.valueOf(ex));
            return false;
        }
    }

    public boolean writeToYml(File file, String path, List list)
    {
        if(!file.getName().endsWith(".yml")) return false;
        FileConfiguration f = YamlConfiguration.loadConfiguration(file);
        f.set(path, list);
        try {
            f.save(file);
            return true;
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, String.valueOf(ex));
            return false;
        }
    }

    public String readFromYml(File file, String path)
    {
        try {
            String fileType = FilenameUtils.getExtension(file.getName());
            if (!fileType.endsWith("yml")) {
                throw new IncorrectFileType("Incorrect file type: '" + fileType + "'");
            }

            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            return configuration.getString(path);
        } catch(IncorrectFileType ex) {
            plugin.getLogger().log(Level.SEVERE, String.valueOf(ex));

            if(new PluginConfig(plugin).debug) {
                this.writeToFile(new File(plugin.getDataFolder(), "debug.log"), String.valueOf(ex));
            }

            return "error";
        }
    }

    public List readFromYmlList(File file, String path)
    {
        try {
            plugin.getLogger().log(Level.FINEST, "Reading " + file.getAbsolutePath());
            String fileType = FilenameUtils.getExtension(file.getName());
            if (!fileType.endsWith("yml")) {
                throw new IncorrectFileType("Incorrect file type: '" + fileType + "'");
            }

            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            return configuration.getList(path);
        } catch(IncorrectFileType ex) {
            plugin.getLogger().log(Level.SEVERE, String.valueOf(ex));

            if(new PluginConfig(plugin).debug) {
                this.writeToFile(new File(plugin.getDataFolder(), "debug.log"), String.valueOf(ex));
            }

            return null;
        }
    }

    public void downloadLib(URL url) throws IOException {
        try(InputStream in = url.openStream();
            ReadableByteChannel rbc = Channels.newChannel(in);
            FileOutputStream fos = new FileOutputStream("plugins/" + url.toString().split("/")[url.toString().split("/").length-1])) {
                fos.getChannel().transferFrom(rbc, 0,Long.MAX_VALUE);
            }
    }
}
