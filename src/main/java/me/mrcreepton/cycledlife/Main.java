package me.mrcreepton.cycledlife;

import me.mrcreepton.cycledlife.commands.TimeExecutor;
import me.mrcreepton.cycledlife.configs.MainConfig;
import me.mrcreepton.cycledlife.configs.WorldConfig;
import me.mrcreepton.cycledlife.events.PlayerJoinEvent;
import me.mrcreepton.cycledlife.events.PlayerWorldChangeEvent;
import me.mrcreepton.cycledlife.models.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.World;
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

        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerWorldChangeEvent(), this);

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
                            if (lastWorld.endsWith("_the_end_CL")) {
                                if (worlds.size() > 0) {
                                    lastWorld = worlds.iterator().next();
                                    worlds.remove(lastWorld);

                                    loadWorld(lastWorld, 0);
                                } else {
                                    getLogger().info("Loading done!");
                                    init();
                                }
                            }
                            else if (lastWorld.endsWith("_nether_CL"))
                            {
                                lastWorld = lastWorld.split("_nether_CL")[0] + "_the_end_CL";
                                loadWorld(lastWorld, 2);
                            }
                            else
                            {
                                lastWorld = lastWorld.split("_CL")[0] + "_nether_CL";
                                loadWorld(lastWorld, 1);
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

    private void loadWorld(String world, int worldType)
    {
            WorldCreator wc = new WorldCreator(world);

            if (worldType == 0)
                wc.environment(World.Environment.NORMAL);
            else if (worldType == 1)
                wc.environment(World.Environment.NETHER);
            else
                wc.environment(World.Environment.THE_END);

            wc.createWorld();

            getLogger().info("Loading " + lastWorld);
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
