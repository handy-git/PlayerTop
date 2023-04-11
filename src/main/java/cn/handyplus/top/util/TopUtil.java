package cn.handyplus.top.util;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.hook.HdUtil;
import cn.handyplus.top.hook.PlaceholderApiUtil;
import cn.handyplus.top.param.PlayerPapiHd;
import cn.handyplus.top.service.TopPapiPlayerService;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author handy
 */
public class TopUtil {

    /**
     * 根据类型获取模板
     *
     * @param topTypeEnum   类型
     * @param newLore       替换的lore
     * @param topPapiPlayer 数据
     * @param rank          排行
     * @return 新值
     * @since 1.0.3
     */
    public static String getContent(PlayerTopTypeEnum topTypeEnum, String newLore, TopPapiPlayer topPapiPlayer, Integer rank) {
        String content = newLore.replace("${content}", topPapiPlayer.getVault().toString())
                .replace("${player}", topPapiPlayer.getPlayerName())
                .replace("${rank}", rank.toString());
        content = PlaceholderApiUtil.set(UUID.fromString(topPapiPlayer.getPlayerUuid()), content);
        return BaseUtil.replaceChatColor(content);
    }

    /**
     * 根据类型获取模板
     *
     * @param lore          替换的lore
     * @param topPapiPlayer 玩家排行数据
     * @param rank          排行
     * @return 新值
     * @since 1.0.3
     */
    public static String getPapiContent(String lore, TopPapiPlayer topPapiPlayer, int rank) {
        String content = lore.replace("${content}", topPapiPlayer.getVault().toString()).replace("${player}", topPapiPlayer.getPlayerName()).replace("${rank}", rank + "");
        content = PlaceholderApiUtil.set(UUID.fromString(topPapiPlayer.getPlayerUuid()), content);
        return BaseUtil.replaceChatColor(content);
    }

    /**
     * 生成全息图
     *
     * @param topTypeEnum 类型
     * @param location    生成位置
     */
    public static PlayerPapiHd createHd(PlayerTopTypeEnum topTypeEnum, Location location) {
        String type = topTypeEnum.getType();
        int line = ConfigUtil.FORMAT_CONFIG.getInt("hdFormat." + type + ".line", 10);
        String material = ConfigUtil.FORMAT_CONFIG.getString("hdFormat." + type + ".material");
        String title = ConfigUtil.FORMAT_CONFIG.getString("hdFormat." + type + ".title");
        String lore = ConfigUtil.FORMAT_CONFIG.getString("hdFormat." + type + ".lore", "");

        // 判断mcMmo
        if (type.contains(PlayerTopTypeEnum.MC_MMO.getType())) {
            line = ConfigUtil.FORMAT_CONFIG.getInt("hdFormat." + PlayerTopTypeEnum.MC_MMO.getType() + ".line", 10);
            material = ConfigUtil.FORMAT_CONFIG.getString("hdFormat." + PlayerTopTypeEnum.MC_MMO.getType() + ".material");
            title = ConfigUtil.FORMAT_CONFIG.getString("hdFormat." + PlayerTopTypeEnum.MC_MMO.getType() + ".title", "");
            title = title.replace("${mcMmo}", topTypeEnum.getName());
            lore = ConfigUtil.FORMAT_CONFIG.getString("hdFormat." + PlayerTopTypeEnum.MC_MMO.getType() + ".lore", "");
        }
        // 判断jobs
        String jobs = "jobs";
        if (type.contains(jobs)) {
            line = ConfigUtil.FORMAT_CONFIG.getInt("hdFormat." + jobs + ".line", 10);
            material = ConfigUtil.FORMAT_CONFIG.getString("hdFormat." + jobs + ".material");
            title = ConfigUtil.FORMAT_CONFIG.getString("hdFormat." + jobs + ".title", "");
            title = title.replace("${jobs}", topTypeEnum.getName());
            lore = ConfigUtil.FORMAT_CONFIG.getString("hdFormat." + jobs + ".lore", "");
        }
        List<TopPapiPlayer> topPlayerList = TopPapiPlayerService.getInstance().page(topTypeEnum.getType(), 1, line);
        List<String> textLineList = new ArrayList<>();
        if (StrUtil.isNotEmpty(title)) {
            textLineList.add(title);
        }
        // 判断有数据 进行构建行
        if (CollUtil.isNotEmpty(topPlayerList)) {
            for (int i = 0; i < topPlayerList.size(); i++) {
                textLineList.add(getContent(topTypeEnum, lore, topPlayerList.get(i), i + 1));
            }
        }
        // 保存全息配置
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".enable", true, Collections.singletonList("是否开启"), "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".world", Objects.requireNonNull(location.getWorld()).getName(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".x", location.getX(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".y", location.getY(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".z", location.getZ(), null, "/hologram.yml");
        ConfigUtil.HD_CONFIG = HandyConfigUtil.load("hologram.yml");
        // 全息配置
        return PlayerPapiHd.builder().textLineList(textLineList).location(location).material(material).build();
    }

    /**
     * 删除全息
     *
     * @param type 类型
     */
    public static void deleteHd(String type) {
        String world = ConfigUtil.HD_CONFIG.getString(type + ".world", "");
        double x = ConfigUtil.HD_CONFIG.getDouble(type + ".x");
        double y = ConfigUtil.HD_CONFIG.getDouble(type + ".y");
        double z = ConfigUtil.HD_CONFIG.getDouble(type + ".z");
        HdUtil.delete(new Location(Bukkit.getWorld(world), x, y, z));
    }

}
