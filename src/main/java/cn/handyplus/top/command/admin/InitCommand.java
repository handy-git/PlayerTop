package cn.handyplus.top.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.top.util.ConfigUtil;
import cn.handyplus.top.util.TopTaskUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ConfigUtil.init();
        TopTaskUtil.setToDataToLock(sender, false);
    }

}