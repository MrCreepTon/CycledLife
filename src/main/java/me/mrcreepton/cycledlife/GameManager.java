package me.mrcreepton.cycledlife;

import me.mrcreepton.cycledlife.configs.MainConfig;
import me.mrcreepton.cycledlife.configs.WorldConfig;
import me.mrcreepton.cycledlife.models.PlayerModel;
import me.mrcreepton.cycledlife.models.SpawnData;
import me.mrcreepton.cycledlife.utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameManager {

    public static boolean isLoaded = false;
    public static int time = 1800;
    public static int totalWorldsCreated = 0;
    public static List<PlayerModel> players = new ArrayList<>();

    public static boolean isAnyPlayerInWorld(String world)
    {
        for (PlayerModel player : players)
        {
            if (Bukkit.getServer().getPlayer(player.getPlayerName()).getWorld().getName().equals(world))
                return true;
        }
        return false;
    }

    public static PlayerModel getPlayerModel(String playerName)
    {
        for (PlayerModel playerModel : players)
        {
            if (playerModel.getPlayerName().equals(playerName))
                return playerModel;
        }
        return null;
    }

    public static SpawnData getData()
    {
        World world = getEmptyWorld();
        WorldConfig config = new WorldConfig((Main)Bukkit.getPluginManager().getPlugin("CycledLife"));

        FileConfiguration fConfig = config.getConfig();
        String worldName = world.getName();
        String path = "worlds." + worldName + ".";

        String armor = "worlds." + worldName + ".armor";
        String items = "worlds." + worldName + ".items";
        String enderchest = "worlds." + worldName + ".enderchest";

        ItemStack[] armorContent = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            if(config.getConfig().getItemStack(armor + "." + i) != null) {
                armorContent[i] = config.getConfig().getItemStack(armor + "." + i);
            }
        }

        ItemStack[] itemsContent = new ItemStack[41];
        for (int i = 0; i < 41; i++) {
            itemsContent[i] = config.getConfig().getItemStack(items + "." + i);
        }

        ItemStack[] enderChestContent = new ItemStack[27];
        for (int i = 0; i < 27; i++) {
            enderChestContent[i] = config.getConfig().getItemStack(enderchest + "." + i);
        }

        SpawnData spawnData = new SpawnData();
        spawnData.setHealth(fConfig.getDouble(path + "health"));
        spawnData.setSatiety(fConfig.getInt(path + "satiety"));
        spawnData.setX(Float.parseFloat(fConfig.getString(path + "x")));
        spawnData.setY(Float.parseFloat(fConfig.getString(path + "y")));
        spawnData.setZ(Float.parseFloat(fConfig.getString(path + "z")));
        spawnData.setYaw(Float.parseFloat(fConfig.getString(path + "yaw")));
        spawnData.setPitch(Float.parseFloat(fConfig.getString(path + "pitch")));
        spawnData.setSpawnx(Float.parseFloat(fConfig.getString(path + "spawnx")));
        spawnData.setSpawny(Float.parseFloat(fConfig.getString(path + "spawny")));
        spawnData.setSpawnz(Float.parseFloat(fConfig.getString(path + "spawnz")));
        spawnData.setSpawnyaw(Float.parseFloat(fConfig.getString(path + "spawnyaw")));
        spawnData.setSpawnpitch(Float.parseFloat(fConfig.getString(path + "spawnpitch")));
        spawnData.setEffects((Collection<PotionEffect>) fConfig.get(path + "effects"));
        spawnData.setItems(itemsContent);
        spawnData.setEnderchest(enderChestContent);
        spawnData.setArmor(armorContent);
        spawnData.setWorld(world);

        return spawnData;

    }

    public static World getEmptyWorld()
    {
        for (World world : Bukkit.getServer().getWorlds())
        {
            String worldName = world.getName();
            Bukkit.getLogger().info(worldName);
            if (worldName.endsWith("CL"))
            {
                if (!isAnyPlayerInWorld(worldName))
                    return Bukkit.getServer().getWorld(worldName);
            }
        }
        return generateNewWorld();
    }

    public static World generateNewWorld()
    {
        GameManager.totalWorldsCreated++;

        MainConfig mainConfig = new MainConfig((Main)Bukkit.getPluginManager().getPlugin("CycledLife"));
        mainConfig.getConfig().set("totalWorlds", GameManager.totalWorldsCreated);
        mainConfig.saveConfig();

        WorldCreator worldCreator = new WorldCreator("world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL");
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.type(WorldType.NORMAL);

        World world = worldCreator.createWorld();

        WorldConfig config = new WorldConfig((Main)Bukkit.getPluginManager().getPlugin("CycledLife"));

        Location location = LocationUtils.getRandomLocation(-1000, 1000, world);

        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".health", 20D);
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".satiety", 20D);
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".armor", null);
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".effects", null);
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".items", null);
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".enderchest", null);
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".x", location.getX());
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".y", location.getY());
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".z", location.getZ());
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".yaw", location.getYaw());
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".pitch", location.getPitch());
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".spawnx", location.getX());
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".spawny", location.getY());
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".spawnz", location.getZ());
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".spawnyaw", location.getYaw());
        config.getConfig().set("worlds." + "world_" + String.valueOf(GameManager.totalWorldsCreated) + "_CL" + ".spawnpitch", location.getPitch());

        config.saveConfig();

        return world;
    }

    public static int getTime() {
        return time;
    }

    public static void setTime(int time) {
        GameManager.time = time;
    }

}
