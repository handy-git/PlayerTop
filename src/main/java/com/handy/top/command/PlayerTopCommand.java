package com.handy.top.command;

import com.handy.lib.annotation.HandyCommand;
import com.handy.lib.command.HandyCommandFactory;
import com.handy.lib.util.BaseUtil;
import com.handy.top.constants.TabListEnum;
import com.handy.top.util.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 命令
 *
 * @author handy
 */
@HandyCommand(name = "playerTop")
public class PlayerTopCommand implements TabExecutor {
    private final static String PERMISSION = "playerTop.reload";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 判断指令是否正确
        if (args.length < 1) {
            return sendHelp(sender);
        }
        boolean rst = HandyCommandFactory.getInstance().onCommand(sender, cmd, label, args, BaseUtil.getLangMsg("noPermission"));
        if (!rst) {
            return sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands;
        if (!sender.hasPermission(PERMISSION)) {
            commands = new ArrayList<>();
        } else {
            commands = TabListEnum.returnList(args, args.length);
        }
        if (commands == null) {
            return null;
        }
        StringUtil.copyPartialMatches(args[args.length - 1].toLowerCase(), commands, completions);
        Collections.sort(completions);
        return completions;
    }

    /**
     * 发送帮助
     *
     * @param sender 发送人
     * @return 消息
     */
    private Boolean sendHelp(CommandSender sender) {
        if (!sender.hasPermission(PERMISSION)) {
            return true;
        }
        List<String> helps = ConfigUtil.LANG_CONFIG.getStringList("helps");
        for (String help : helps) {
            sender.sendMessage(BaseUtil.replaceChatColor(help));
        }
        return true;
    }

}