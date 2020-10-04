package me.mrcreepton.cycledlife.models;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.List;

public class SpawnData {

    private double health;
    private int satiety;
    private float x = 0;
    private float y = 0;
    private float z = 0;
    private float pitch = 0;
    private float yaw = 0;
    private float spawnx = 0;
    private float spawny = 0;
    private float spawnz = 0;
    private float spawnpitch = 0;
    private float spawnyaw = 0;
    private int worldtype = 0;
    private ItemStack[] enderchest;
    private ItemStack[] items;
    private Collection<PotionEffect> effects;
    private ItemStack[] armor;
    private String world;

    public SpawnData() {
    }

    public ItemStack[] getEnderchest() {
        return enderchest;
    }

    public void setEnderchest(ItemStack[] enderchest) {
        this.enderchest = enderchest;
    }

    public int getWorldtype() {
        return worldtype;
    }

    public void setWorldtype(int worldtype) {
        this.worldtype = worldtype;
    }

    public float getSpawnx() {
        return spawnx;
    }

    public void setSpawnx(float spawnx) {
        this.spawnx = spawnx;
    }

    public float getSpawny() {
        return spawny;
    }

    public void setSpawny(float spawny) {
        this.spawny = spawny;
    }

    public float getSpawnz() {
        return spawnz;
    }

    public void setSpawnz(float spawnz) {
        this.spawnz = spawnz;
    }

    public float getSpawnpitch() {
        return spawnpitch;
    }

    public void setSpawnpitch(float spawnpitch) {
        this.spawnpitch = spawnpitch;
    }

    public float getSpawnyaw() {
        return spawnyaw;
    }

    public void setSpawnyaw(float spawnyaw) {
        this.spawnyaw = spawnyaw;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getSatiety() {
        return satiety;
    }

    public void setSatiety(int satiety) {
        this.satiety = satiety;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public Collection<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(Collection<PotionEffect> effects) {
        this.effects = effects;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }
}
