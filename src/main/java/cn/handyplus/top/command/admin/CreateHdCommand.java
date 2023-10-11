package cn.handyplus.top.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.hook.HdUtil;
import cn.handyplus.top.param.PlayerPapiHd;
import cn.handyplus.top.util.TopUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 创建全息图配置
 *
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
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getLangMsg("noPlayerFailureMsg"));
        // 是否加载全息图
        AssertUtil.notTrue(!PlayerTop.USE_HOLOGRAPHIC_DISPLAYS && !PlayerTop.USE_CMI, sender, BaseUtil.getLangMsg("HolographicDisplaysFailureMsg"));
        // 获取类型
        PlayerTopTypeEnum topTypeEnum = PlayerTopTypeEnum.getType(args[1]);
        AssertUtil.notNull(topTypeEnum, sender, BaseUtil.getLangMsg("typeFailureMsg"));
        // 进行生成
        Location playerLocation = player.getLocation();
        Location location = new Location(player.getWorld(), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
        // 删除现有全息
        TopUtil.deleteHd(topTypeEnum.getType());
        // 生成全息数据
        PlayerPapiHd playerPapiHd = TopUtil.createHd(topTypeEnum, location);
        if (playerPapiHd == null) {
            return;
        }
        // 创建全息
        HdUtil.create(playerPapiHd.getTextLineList(), playerPapiHd.getLocation(), playerPapiHd.getMaterial(), playerPapiHd.getCustomModelData());
    }

}