package com.handy.top.command.admin;

import com.handy.lib.command.IHandyCommandEvent;
import com.handy.lib.util.BaseUtil;
import com.handy.top.util.ConfigUtil;
import com.handy.top.util.TopTaskUtil;
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
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ConfigUtil.init();
        TopTaskUtil.execute();
        sender.sendMessage(BaseUtil.getLangMsg("reloadMsg"));
    }

}