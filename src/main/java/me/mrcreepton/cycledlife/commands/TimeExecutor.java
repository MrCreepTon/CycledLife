package me.mrcreepton.cycledlife.commands;

import me.mrcreepton.cycledlife.GameManager;
import me.mrcreepton.cycledlife.models.PlayerModel;
import me.mrcreepton.cycledlife.models.TimeData;
import me.mrcreepton.cycledlife.utils.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TimeExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            PlayerModel model = GameManager.getPlayerModel(sender.getName());
            if (model != null)
            {
                int time = model.getTimeLeft();
                TimeData timeData = TimeUtils.getTimeFromSeconds(time);
                sender.sendMessage("§7§l[§8§lCycled§c§lLife§7§l]: §fУ тебя осталось §c" + String.valueOf(timeData.getHours()) + " часов§f, §c" + String.valueOf(timeData.getMinutes()) + " минут §fи §c" + String.valueOf(timeData.getSeconds()) + " секунд");
            }
        }
        return true;
    }

}
