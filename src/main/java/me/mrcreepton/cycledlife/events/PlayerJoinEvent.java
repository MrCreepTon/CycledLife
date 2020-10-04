package me.mrcreepton.cycledlife.events;

import me.mrcreepton.cycledlife.GameManager;
import me.mrcreepton.cycledlife.Main;
import me.mrcreepton.cycledlife.configs.WorldConfig;
import me.mrcreepton.cycledlife.models.PlayerModel;
import me.mrcreepton.cycledlife.models.SpawnData;
import me.mrcreepton.cycledlife.models.TimeData;
import me.mrcreepton.cycledlife.utils.LocationUtils;
import me.mrcreepton.cycledlife.utils.TimeUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerJoinEvent implements Listener {

    @EventHandler
    public static void onPlayerLogin(PlayerLoginEvent e)
    {
        if (!GameManager.isLoaded)
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Сервер загружается, подождите!");
    }

    @EventHandler
    public static void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SpawnData data = GameManager.getData();

        player.setHealth(data.getHealth());
        player.setFoodLevel(data.getSatiety());

        String worldName = data.getWorld();

        World defaultWorld = Bukkit.getServer().getWorld(worldName);

        if (data.getWorldtype() == 1)
            worldName = worldName.split("_CL")[0] + "_nether_CL";
        else if (data.getWorldtype() == 2)
            worldName = worldName.split("_CL")[0] + "_the_end_CL";

        World world = Bukkit.getServer().getWorld(worldName);

        Location location = new Location(world, data.getX(), data.getY(), data.getZ(), data.getYaw(), data.getPitch());
        player.teleport(location);

        Location spawnLocation = new Location(defaultWorld, data.getSpawnx(), data.getSpawny(), data.getSpawnz(), data.getSpawnyaw(), data.getSpawnpitch());
        player.setBedSpawnLocation(spawnLocation, true);

        player.getInventory().clear();
        player.getEquipment().clear();
        player.getEnderChest().clear();

        if (data.getItems() != null) {
            for (ItemStack item : data.getItems()) {
                if (item != null)
                    player.getInventory().addItem(item);
            }
        }

        player.setExp(data.getXp());
        player.setLevel(data.getLevel());

        player.getEnderChest().setContents(data.getEnderchest());
        player.getEquipment().setArmorContents(data.getArmor());
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
        try {
            player.addPotionEffects(data.getEffects());
        }
        catch (Exception e)
        {
            // ok
        }

        PlayerModel playerModel = new PlayerModel(player.getName(), defaultWorld.getName());
        GameManager.players.add(playerModel);

        TimeData timeData = TimeUtils.getTimeFromSeconds(playerModel.getTimeLeft());

        Bukkit.getServer().broadcastMessage("§f§l[§8§lCycled§c§lLife§f§l]: §fИгрок §c" + player.getName() + " §fподключился к миру §c№" + worldName.split("_")[1]);

        player.sendMessage("§f§l[§8§lCycled§c§lLife§f§l]: §fОкей, у тебя есть §c" + String.valueOf(timeData.getHours()) + " часов§f, §c" + String.valueOf(timeData.getMinutes()) + " минут §fи §c" + String.valueOf(timeData.getSeconds()) + " секунд§f. Тебе выданы все ресурсы предыдущего игрока и все остальное. Удачи!");
        player.sendMessage("§f§l[§8§lCycled§c§lLife§f§l]: §fP.S: Для того, чтобы узнать время, используй §c/time");
    }

    private static List<String> kickedPlayers = new ArrayList<>();

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e)
    {

        Player player = e.getPlayer();
        PlayerModel model = GameManager.getPlayerModel(player.getName());

        saveWorldForPlayer(player);
        
        GameManager.players.remove(model);

        if (kickedPlayers.contains(player.getName()))
        {
            kickedPlayers.remove(player.getName());
            Bukkit.getServer().broadcastMessage("§f§l[§8§lCycled§c§lLife§f§l]: §fИгрок §c" + player.getName() + " §fпокинул игру §c(Кончилось время)");
        }
        else
        {
            Bukkit.getServer().broadcastMessage("§f§l[§8§lCycled§c§lLife§f§l]: §fИгрок §c" + player.getName() + " §fпокинул игру §c(Вышел)");
        }
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Перед следующей игрой нужно подождать минимум 3 часа", new Date(System.currentTimeMillis() + 3*60*60*1000 - model.getTimeLeft() * 1000), null);
    }

    @EventHandler
    public static void onPlayerKickEvent(PlayerKickEvent e)
    {
        Player player = e.getPlayer();
        kickedPlayers.add(e.getPlayer().getName());
    }

    private static void saveWorldForPlayer(Player player)
    {
        PlayerModel model = GameManager.getPlayerModel(player.getName());

        WorldConfig config = new WorldConfig((Main) Bukkit.getPluginManager().getPlugin("CycledLife"));
        Location location = player.getLocation();

        Location spawnLocation = player.getBedSpawnLocation();

        if (spawnLocation == null)
        {
            spawnLocation = LocationUtils.getRandomLocation(-1000, 1000, player.getWorld());
        }

        config.getConfig().set("worlds." + model.getWorldName()+ ".health", player.getHealth());
        config.getConfig().set("worlds." + model.getWorldName()+ ".satiety", player.getFoodLevel());
        config.getConfig().set("worlds." + model.getWorldName()+ ".effects", player.getActivePotionEffects());
        for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
            config.getConfig().set("worlds." + model.getWorldName()+ ".armor." + i, player.getEquipment().getArmorContents()[i]);
        }
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            config.getConfig().set("worlds." + model.getWorldName()+ ".items." + i, player.getInventory().getContents()[i]);
        }
        for (int i = 0; i < player.getEnderChest().getContents().length; i++) {
            config.getConfig().set("worlds." + model.getWorldName()+ ".enderchest." + i, player.getEnderChest().getContents()[i]);
        }
        config.getConfig().set("worlds." + model.getWorldName()+ ".x", location.getX());
        config.getConfig().set("worlds." + model.getWorldName()+ ".y", location.getY());
        config.getConfig().set("worlds." + model.getWorldName()+ ".z", location.getZ());
        config.getConfig().set("worlds." + model.getWorldName()+ ".yaw", location.getYaw());
        config.getConfig().set("worlds." + model.getWorldName()+ ".pitch", location.getPitch());

        config.getConfig().set("worlds." + model.getWorldName()+ ".spawnx", spawnLocation.getX());
        config.getConfig().set("worlds." + model.getWorldName()+ ".spawny", spawnLocation.getY());
        config.getConfig().set("worlds." + model.getWorldName()+ ".spawnz", spawnLocation.getZ());
        config.getConfig().set("worlds." + model.getWorldName()+ ".spawnyaw", spawnLocation.getYaw());
        config.getConfig().set("worlds." + model.getWorldName()+ ".spawnpitch", spawnLocation.getPitch());
        config.getConfig().set("worlds." + model.getWorldName()+ ".level", player.getLevel());
        config.getConfig().set("worlds." + model.getWorldName()+ ".xp", player.getExp());

        if (player.getWorld().getName().endsWith("_nether_CL"))
            config.getConfig().set("worlds." + model.getWorldName()+ ".worldtype", 1);
        else if (player.getWorld().getName().endsWith("_the_end_CL"))
            config.getConfig().set("worlds." + model.getWorldName()+ ".worldtype", 2);
        else
            config.getConfig().set("worlds." + model.getWorldName()+ ".worldtype", 0);

        config.saveConfig();
    }

}
