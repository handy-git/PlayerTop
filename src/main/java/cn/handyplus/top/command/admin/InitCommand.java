package cn.handyplus.top.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.util.ConfigUtil;
import cn.handyplus.top.util.TopTaskUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 初始化离线数据
 *
 * @author handy
 */
public class InitCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "init";
    }

    @Override
    public String permission() {
        return "playerTop.init";
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ConfigUtil.init();
                TopTaskUtil.setToDataToLock(sender, false);
            }
        }.runTaskAsynchronously(PlayerTop.getInstance());
    }

}