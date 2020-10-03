package me.mrcreepton.cycledlife.configs;

import me.mrcreepton.cycledlife.GameManager;
import me.mrcreepton.cycledlife.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainConfig  {

    private Main plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    private String fileName = "data.yml";

    public MainConfig(Main plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig()
    {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), fileName);
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource(fileName);
        if (defaultStream == null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig()
    {
        if (this.dataConfig == null)
            reloadConfig();
        return this.dataConfig;
    }

    public void saveConfig()
    {
        if (this.dataConfig == null || this.configFile == null)
            return;
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig()
    {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), fileName);

        if (!this.configFile.exists())
            this.plugin.saveResource(fileName, false);
    }

}
