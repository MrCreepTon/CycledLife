package me.mrcreepton.cycledlife;

import me.mrcreepton.cycledlife.commands.TimeExecutor;
import me.mrcreepton.cycledlife.configs.MainConfig;
import me.mrcreepton.cycledlife.configs.WorldConfig;
import me.mrcreepton.cycledlife.events.PlayerJoinEvent;
import me.mrcreepton.cycledlife.models.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        getServer().getMessenger().registerOutgoingPluginChannel(this, "CLChannel");

        loadWorlds();

    }

    int taskId = 0;
    String lastWorld = "world";

    private void loadWorlds()
    {
        try {
            Set<String> worlds = new WorldConfig(this).getConfig().getConfigurationSection("worlds").getKeys(false);
            if (worlds.size() > 0) {
                taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                    @Override
                    public void run() {
                        if (Bukkit.getServer().getWorld(lastWorld) != null) {
                            getLogger().info(lastWorld + " has been loaded.");
                            if (worlds.size() > 0) {
                                lastWorld = worlds.iterator().next();
                                worlds.remove(lastWorld);

                                WorldCreator wc = new WorldCreator(lastWorld);
                                wc.createWorld();

                                getLogger().info("Loading " + lastWorld);
                            } else {
                                getLogger().info("Loading done!");
                                init();
                            }
                        }
                    }
                }, 10L, 10L);
            }
        }
        catch (NullPointerException e)
        {
            getLogger().info("Nothing to load, ok");
            init();
        }
    }

    private void init()
    {
        getCommand("time").setExecutor(new TimeExecutor());

        getServer().getScheduler().cancelTask(taskId);
        GameManager.isLoaded = true;

        MainConfig config = new MainConfig(this);

        if (config.getConfig().contains("totalWorlds"))
            GameManager.totalWorldsCreated = config.getConfig().getInt("totalWorlds");
        else
            config.getConfig().set("totalWorlds", 0);

        if (config.getConfig().contains("time"))
            GameManager.time = config.getConfig().getInt("time");
        else
            config.getConfig().set("time", GameManager.time);

        config.saveConfig();

        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (PlayerModel playerModel : GameManager.players)
                {
                    playerModel.setTimeLeft(playerModel.getTimeLeft() - 1);
                    if (playerModel.getTimeLeft() <= 0)
                        getServer().getPlayer(playerModel.getPlayerName()).kickPlayer("Вы поиграли 30 минут. Теперь нужно подождать минимум 3 часа.");
                }
            }
        }, 20L, 20L);
    }

    @Override
    public void onDisable() {

    }
}
