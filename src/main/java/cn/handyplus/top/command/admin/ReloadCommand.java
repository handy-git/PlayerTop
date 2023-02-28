package cn.handyplus.top.command.admin;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.util.ConfigUtil;
import cn.handyplus.top.util.TopTaskUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 重载配置
 *
 * @author handy
 */
public class ReloadCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "reload";
    }

    @Override
    public String permission() {
        return "playerTop.reload";
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ConfigUtil.init();
                TopTaskUtil.setToDataToLock(sender);
                MessageApi.sendMessage(sender, BaseUtil.getLangMsg("reloadMsg"));
            }
        }.runTaskAsynchronously(PlayerTop.getInstance());
    }

}