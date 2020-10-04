package me.mrcreepton.cycledlife.events;

import me.mrcreepton.cycledlife.GameManager;
import me.mrcreepton.cycledlife.Main;
import me.mrcreepton.cycledlife.configs.WorldConfig;
import me.mrcreepton.cycledlife.models.PlayerModel;
import me.mrcreepton.cycledlife.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerWorldChangeEvent implements Listener {

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
    public static void onPlayerPortal(PlayerPortalEvent e)
    {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
        {
            Player player = e.getPlayer();
            PlayerModel model = GameManager.getPlayerModel(player.getName());

            World defaultWorld = Bukkit.getServer().getWorld(model.getWorldName());
            World world = player.getWorld();

            Location location = player.getLocation().clone();

            if (world.getName().endsWith("_nether_CL")) {
                location.setWorld(defaultWorld);
                location.setX(e.getFrom().getBlockX() * 8);
                location.setZ(e.getFrom().getBlockZ() * 8);
            }
            else
            {
                location.setWorld(Bukkit.getWorld(world.getName().split("_CL")[0] + "_nether_CL"));
                location.setX(e.getFrom().getBlockX() / 8);
                location.setZ(e.getFrom().getBlockZ() / 8);
            }

            e.getPortalTravelAgent().setCanCreatePortal(true);
            e.getPortalTravelAgent().setCanCreatePortal(true);
            e.setTo(e.getPortalTravelAgent().findOrCreate(location));
        }
        else if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL)
        {
            Player player = e.getPlayer();
            PlayerModel model = GameManager.getPlayerModel(player.getName());

            World defaultWorld = Bukkit.getServer().getWorld(model.getWorldName());
            World world = player.getWorld();

            Location location = player.getLocation().clone();

            if (world.getName().endsWith("_the_end_CL")) {
                location.setWorld(defaultWorld);
                location.setX(player.getBedSpawnLocation().getBlockX());
                location.setY(player.getBedSpawnLocation().getBlockY());
                location.setZ(player.getBedSpawnLocation().getBlockZ());
            }
            else
            {
                location.setWorld(Bukkit.getWorld(world.getName().split("_CL")[0] + "_the_end_CL"));
                location.setX(100);
                location.setY(50);
                location.setZ(0);

                Block block = location.getBlock();
                for (int x = block.getX() - 2; x <= block.getX() + 2; x++) {
                    for (int z = block.getZ() - 2; z <= block.getZ() + 2; z++) {
                        Block platformBlock = location.getWorld().getBlockAt(x, block.getY() - 1, z);
                        if (platformBlock.getType() != Material.OBSIDIAN) {
                            platformBlock.setType(Material.OBSIDIAN);
                        }
                        for (int yMod = 1; yMod <= 3; yMod++) {
                            Block b = platformBlock.getRelative(BlockFace.UP, yMod);
                            if (b.getType() != Material.AIR) {
                                b.setType(Material.AIR);
                            }
                        }
                    }
                }
            }

            e.setTo(location);
        }
    }

}
