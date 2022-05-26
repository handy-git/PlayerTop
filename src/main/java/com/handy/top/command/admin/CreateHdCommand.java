package com.handy.top.command.admin;

import com.handy.lib.command.IHandyCommandEvent;
import com.handy.lib.core.CollUtil;
import com.handy.lib.core.StrUtil;
import com.handy.lib.util.AssertUtil;
import com.handy.lib.util.BaseUtil;
import com.handy.lib.util.HandyConfigUtil;
import com.handy.top.PlayerTop;
import com.handy.top.constants.PlayerTopTypeEnum;
import com.handy.top.enter.TopPlayer;
import com.handy.top.hook.HolographicDisplaysUtil;
import com.handy.top.service.TopPlayerService;
import com.handy.top.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        createHd(topTypeEnum, location);
    }

    /**
     * 生成全息图
     *
     * @param topTypeEnum 类型
     * @param location    生成位置
     */
    public static void createHd(PlayerTopTypeEnum topTypeEnum, Location location) {
        String type = topTypeEnum.getType();
        int line = ConfigUtil.CONFIG.getInt("hdFormat." + type + ".line", 10);
        List<TopPlayer> topPlayerList = TopPlayerService.getInstance().page(topTypeEnum, 1, line);
        // 先进行删除
        HolographicDisplaysUtil.delete(location);
        String material = ConfigUtil.CONFIG.getString("hdFormat." + type + ".material");
        String title = ConfigUtil.CONFIG.getString("hdFormat." + type + ".title");
        String lore = ConfigUtil.CONFIG.getString("hdFormat." + type + ".lore", "");

        List<String> textLineList = new ArrayList<>();
        if (StrUtil.isNotEmpty(title)) {
            textLineList.add(title);
        }
        // 判断有数据 进行构建行
        if (CollUtil.isNotEmpty(topPlayerList)) {
            for (int i = 0; i < topPlayerList.size(); i++) {
                TopPlayer topPlayer = topPlayerList.get(i);
                String newLore = lore.replace("${player}", topPlayer.getPlayerName()).replace("${rank}", (i + 1) + "");
                String content = "";
                switch (topTypeEnum) {
                    case VAULT:
                        content = newLore.replace("${content}", topPlayer.getVault().intValue() + "").replace("${original_content}", topPlayer.getVault() + "");
                        break;
                    case PLAYER_POINTS:
                        content = newLore.replace("${content}", topPlayer.getPlayerPoints() + "").replace("${original_content}", topPlayer.getPlayerPoints() + "");
                        break;
                    case PLAYER_TITLE_COIN:
                        content = newLore.replace("${content}", topPlayer.getPlayerTitleCoin() + "").replace("${original_content}", topPlayer.getPlayerTitleCoin() + "");
                        break;
                    case PLAYER_TITLE_NUMBER:
                        content = newLore.replace("${content}", topPlayer.getPlayerTitleNumber() + "").replace("${original_content}", topPlayer.getPlayerTitleNumber() + "");
                        break;
                    case PLAYER_TASK_COIN:
                        content = newLore.replace("${content}", topPlayer.getPlayerTaskCoin() + "").replace("${original_content}", topPlayer.getPlayerTaskCoin() + "");
                        break;
                    case PLAYER_GUILD_MONEY:
                        content = newLore.replace("${content}", topPlayer.getPlayerGuildMoney() + "").replace("${original_content}", topPlayer.getPlayerGuildMoney() + "");
                        break;
                    default:
                        content = newLore;
                        break;
                }
                textLineList.add(content);
            }
        }
        // 创建全息
        HolographicDisplaysUtil.create(textLineList, location, material);
        // 保存全息配置
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".enable", true, Collections.singletonList("是否开启"), "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".world", Objects.requireNonNull(location.getWorld()).getName(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".x", location.getX(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".y", location.getY(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".z", location.getZ(), null, "/hologram.yml");
        ConfigUtil.HD_CONFIG = HandyConfigUtil.load("hologram.yml");
    }

}