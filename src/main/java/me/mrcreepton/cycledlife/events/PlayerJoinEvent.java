package me.mrcreepton.cycledlife.events;

import me.mrcreepton.cycledlife.GameManager;
import me.mrcreepton.cycledlife.Main;
import me.mrcreepton.cycledlife.configs.WorldConfig;
import me.mrcreepton.cycledlife.models.PlayerModel;
import me.mrcreepton.cycledlife.models.SpawnData;
import me.mrcreepton.cycledlife.utils.LocationUtils;
import me.mrcreepton.cycledlife.utils.SendUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Date;

public class PlayerJoinEvent implements Listener {

    @EventHandler
    public static void onPlayerWorldChange(PlayerChangedWorldEvent e)
    {
        if (!e.getPlayer().getWorld().getName().endsWith("CL")) {
            Location location = LocationUtils.getRandomLocation(-1000, 1000, e.getFrom());

            e.getPlayer().teleport(location);
            e.getPlayer().setBedSpawnLocation(location);

            WorldConfig config = new WorldConfig((Main) Bukkit.getPluginManager().getPlugin("CycledLife"));

            e.getPlayer().sendMessage("§7§l[§8§lCycled§c§lLife§7§l]: §fВидимо, произошла неполадка. Мы вернули тебя обратно в твой мир ^_^");
        }
    }

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

        Location location = new Location(data.getWorld(), data.getX(), data.getY(), data.getZ(), data.getYaw(), data.getPitch());
        player.teleport(location);

        Location spawnLocation = new Location(data.getWorld(), data.getSpawnx(), data.getSpawny(), data.getSpawnz(), data.getSpawnyaw(), data.getSpawnpitch());
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

        PlayerModel playerModel = new PlayerModel(player.getName(), data.getWorld().getName());
        GameManager.players.add(playerModel);

        SendUtils.sendData("CLChannel", "Test12345");

        player.sendMessage("§7§l[§8§lCycled§c§lLife§7§l]: §fОкей, у тебя есть 30 минут. Тебе выданы все ресурсы предыдущего игрока и все остальное. Удачи!");
        player.sendMessage("§7§l[§8§lCycled§c§lLife§7§l]: §fP.S: Для того, чтобы узнать время, используй §8/§ctime");
    }
    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e)
    {
        Player player = e.getPlayer();

        PlayerModel model = GameManager.getPlayerModel(player.getName());

        WorldConfig config = new WorldConfig((Main) Bukkit.getPluginManager().getPlugin("CycledLife"));
        Location location = player.getLocation();

        Location spawnLocation = player.getBedSpawnLocation();

        if (spawnLocation == null)
        {
            spawnLocation = LocationUtils.getRandomLocation(-1000, 1000, e.getPlayer().getWorld());
        }

        config.getConfig().set("worlds." + player.getWorld().getName()+ ".health", player.getHealth());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".satiety", player.getFoodLevel());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".effects", player.getActivePotionEffects());
        for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
            config.getConfig().set("worlds." + player.getWorld().getName()+ ".armor." + i, player.getEquipment().getArmorContents()[i]);
        }
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            config.getConfig().set("worlds." + player.getWorld().getName()+ ".items." + i, player.getInventory().getContents()[i]);
        }
        for (int i = 0; i < player.getEnderChest().getContents().length; i++) {
            config.getConfig().set("worlds." + player.getWorld().getName()+ ".enderchest." + i, player.getEnderChest().getContents()[i]);
        }
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".x", location.getX());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".y", location.getY());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".z", location.getZ());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".yaw", location.getYaw());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".pitch", location.getPitch());

        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawnx", spawnLocation.getX());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawny", spawnLocation.getY());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawnz", spawnLocation.getZ());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawnyaw", spawnLocation.getYaw());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawnpitch", spawnLocation.getPitch());

        config.saveConfig();
        
        GameManager.players.remove(model);

        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Перед следующей игрой нужно подождать минимум 3 часа", new Date(System.currentTimeMillis() + 3*60*60*1000), null);
    }

    @EventHandler
    public static void onPlayerKickEvent(PlayerKickEvent e)
    {
        Player player = e.getPlayer();

        PlayerModel model = GameManager.getPlayerModel(player.getName());

        WorldConfig config = new WorldConfig((Main) Bukkit.getPluginManager().getPlugin("CycledLife"));
        Location location = player.getLocation();

        Location spawnLocation = player.getBedSpawnLocation();

        if (spawnLocation == null)
        {
            spawnLocation = LocationUtils.getRandomLocation(-1000, 1000, e.getPlayer().getWorld());
        }

        config.getConfig().set("worlds." + player.getWorld().getName()+ ".health", player.getHealth());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".satiety", player.getFoodLevel());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".effects", player.getActivePotionEffects());
        for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
            config.getConfig().set("worlds." + player.getWorld().getName()+ ".armor." + i, player.getEquipment().getArmorContents()[i]);
        }
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            config.getConfig().set("worlds." + player.getWorld().getName()+ ".items." + i, player.getInventory().getContents()[i]);
        }
        for (int i = 0; i < player.getEnderChest().getContents().length; i++) {
            config.getConfig().set("worlds." + player.getWorld().getName()+ ".enderchest." + i, player.getEnderChest().getContents()[i]);
        }
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".x", location.getX());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".y", location.getY());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".z", location.getZ());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".yaw", location.getYaw());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".pitch", location.getPitch());

        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawnx", spawnLocation.getX());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawny", spawnLocation.getY());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawnz", spawnLocation.getZ());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawnyaw", spawnLocation.getYaw());
        config.getConfig().set("worlds." + player.getWorld().getName()+ ".spawnpitch", spawnLocation.getPitch());

        config.saveConfig();

        GameManager.players.remove(model);

        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Перед следующей игрой нужно подождать минимум 3 часа", new Date(System.currentTimeMillis() + 3*60*60*1000), null);
    }

}
