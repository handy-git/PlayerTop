package cn.handyplus.top.command.admin;

import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.hook.HdUtil;
import cn.handyplus.top.param.PlayerPapiHd;
import cn.handyplus.top.util.ConfigUtil;
import cn.handyplus.top.util.TopTaskUtil;
import cn.handyplus.top.util.TopUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

/**
 * 移动全息图配置
 *
 * @author handy
 */
public class MoveHdCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "moveHd";
    }

    @Override
    public String permission() {
        return "playerTop.moveHd";
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        // 是否为玩家
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getMsgNotColor("noPlayerFailureMsg"));
        // 是否加载全息图
        AssertUtil.notTrue(!PlayerTop.USE_HOLOGRAPHIC_DISPLAYS && !PlayerTop.USE_CMI, sender, BaseUtil.getMsgNotColor("HolographicDisplaysFailureMsg"));
        String type = args[1];
        // 当前位置
        Location playerLocation = player.getLocation();
        Location location = new Location(player.getWorld(), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
        // 内置类型
        PlayerTopTypeEnum topTypeEnum = PlayerTopTypeEnum.getType(type);
        if (topTypeEnum != null) {
            // 删除现有全息
            TopUtil.deleteHd(topTypeEnum.getType());
            // 生成全息数据
            PlayerPapiHd playerPapiHd = TopUtil.createHd(topTypeEnum, location);
            if (playerPapiHd == null) {
                return;
            }
            // 创建全息
            HdUtil.create(playerPapiHd.getTextLineList(), playerPapiHd.getLocation(), playerPapiHd.getMaterial(), playerPapiHd.getCustomModelData());
            return;
        }
        // 如果是变量,移除%
        if (PlaceholderAPI.containsPlaceholders(type)) {
            type = type.replace("%", "");
        }
        // 变量类型
        Map<String, Object> oneChildPapiMap = ConfigUtil.getPapiOneChildMap();
        if (oneChildPapiMap.get(type) != null) {
            // 删除现有全息
            TopUtil.deletePapiHd(type);
            // 修改全息位置
            TopUtil.movePapiHd(type, location);
            Optional<PlayerPapiHd> playerPapiHdOptional = TopTaskUtil.getPapiData(type, oneChildPapiMap);
            if (playerPapiHdOptional.isPresent()) {
                PlayerPapiHd playerPapiHd = playerPapiHdOptional.get();
                // 创建全息
                HdUtil.create(playerPapiHd.getTextLineList(), playerPapiHd.getLocation(), playerPapiHd.getMaterial(), playerPapiHd.getCustomModelData());
            }
            return;
        }
        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("typeFailureMsg") + ":" + type);
    }

}