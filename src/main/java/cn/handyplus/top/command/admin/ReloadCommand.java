package cn.handyplus.top.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.top.util.ConfigUtil;
import cn.handyplus.top.util.TopTaskUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ConfigUtil.init();
        TopTaskUtil.setToDataToLock(sender, true);
    }

}