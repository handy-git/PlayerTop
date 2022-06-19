package com.handy.top.command.admin;

import com.handy.lib.command.IHandyCommandEvent;
import com.handy.lib.util.AssertUtil;
import com.handy.lib.util.BaseUtil;
import com.handy.top.PlayerTop;
import com.handy.top.constants.PlayerTopTypeEnum;
import com.handy.top.util.TopUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author handy
 */
public class CreateHdCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "createHd";
    }

    @Override
    public String permission() {
        return "playerTop.createHd";
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getLangMsg("paramFailureMsg"));
        // 是否为玩家
        AssertUtil.notPlayer(sender, BaseUtil.getLangMsg("noPlayerFailureMsg"));
        Player player = (Player) sender;
        // 是否加载全息图
        AssertUtil.notTrue(!PlayerTop.USE_HOLOGRAPHIC_DISPLAYS, sender, BaseUtil.getLangMsg("HolographicDisplaysFailureMsg"));
        // 获取类型
        PlayerTopTypeEnum topTypeEnum = PlayerTopTypeEnum.getType(args[1]);
        AssertUtil.notNull(topTypeEnum, sender, BaseUtil.getLangMsg("typeFailureMsg"));
        // 进行生成
        Location playerLocation = player.getLocation();
        Location location = new Location(player.getWorld(), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
        TopUtil.deleteHd(topTypeEnum.getType());
        TopUtil.createHd(topTypeEnum, location);
    }

}