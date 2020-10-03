package me.mrcreepton.cycledlife.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

public class LocationUtils {

    public static Location getRandomLocation(int min, int max, World world) {

        int x = (int) (Math.random() * (max - min)) + min;
        int z;
        if (Math.abs(x) < min) {
            z = (int) (Math.random() * (max - min)) + min;//If x coord is less than the minimum choose a z coord that is within range
            if (new Random().nextBoolean()) {//Top or bottom of range
                z = -z;
            }
        } else {
            z = (int) (Math.random() * (-max - max)) + max;//Else choose any z coord
        }
        int y = 255;
        while (world.getBlockAt(x, y, z).getType() == Material.AIR) {
            y--;
        }
        return world.getBlockAt(x, y + 1, z).getLocation();
    }

}
