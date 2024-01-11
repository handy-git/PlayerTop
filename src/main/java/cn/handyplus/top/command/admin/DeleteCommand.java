package cn.handyplus.top.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.top.service.TopPapiPlayerService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * 删除数据
 *
 * @author handy
 */
public class DeleteCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "delete";
    }

    @Override
    public String permission() {
        return "playerTop.delete";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        // 删除对应排行数据
        int num = TopPapiPlayerService.getInstance().deleteByPapi(args[1]);
        MessageUtil.sendMessage(sender, "&a删除完成,本次删除" + num + "条" + args[1] + "对应数据");
    }

}