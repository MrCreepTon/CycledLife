package me.mrcreepton.cycledlife.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;

public class SendUtils {

    public static void sendData(String channelName, String stringData) {

        ByteArrayDataOutput o = ByteStreams.newDataOutput();
        o.writeUTF("CLSubChannel");
        o.writeUTF(stringData);
        Bukkit.getServer().sendPluginMessage(Bukkit.getPluginManager().getPlugin("CycledLife"), channelName, o.toByteArray());
        Bukkit.getLogger().info("Sended data");
    }

}
