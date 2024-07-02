package cn.handyplus.top.util;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.hologram.HdUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.hook.PlaceholderApiUtil;
import cn.handyplus.top.param.PlayerPapiHd;
import cn.handyplus.top.service.TopPapiPlayerService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

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
     * @param lore          替换的lore
     * @param topPapiPlayer 数据
     * @return 新值
     * @since 1.2.2
     */
    public static String getContent(String lore, TopPapiPlayer topPapiPlayer) {
        String content = lore.replace("${content}", topPapiPlayer.getVault().toString())
                .replace("${player}", topPapiPlayer.getPlayerName())
                .replace("${rank}", topPapiPlayer.getRank().toString());
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
        int customModelData = ConfigUtil.FORMAT_CONFIG.getInt("hdFormat." + type + ".custom-model-data");

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
            for (TopPapiPlayer topPapiPlayer : topPlayerList) {
                textLineList.add(getContent(lore, topPapiPlayer));
            }
        }
        World world = location.getWorld();
        if (world == null) {
            return null;
        }
        // 保存全息配置
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".enable", true, Collections.singletonList("是否开启"), "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".world", world.getName(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".x", location.getX(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".y", location.getY(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".z", location.getZ(), null, "/hologram.yml");
        ConfigUtil.HD_CONFIG = HandyConfigUtil.load("hologram.yml");
        // 全息配置
        return PlayerPapiHd.builder().textLineList(textLineList).location(location).material(material).customModelData(customModelData).build();
    }

    /**
     * 移动Papi全息图
     *
     * @param type     类型
     * @param location 生成位置
     */
    public static void movePapiHd(String type, Location location) {
        // 设置全息配置
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".enable", true, null, "/papi.yml");
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".world", Objects.requireNonNull(location.getWorld()).getName(), null, "/papi.yml");
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".x", location.getX(), null, "/papi.yml");
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".y", location.getY(), null, "/papi.yml");
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".z", location.getZ(), null, "/papi.yml");
        // 重新读取配置
        ConfigUtil.PAPI_CONFIG = HandyConfigUtil.load("papi.yml");
    }

    /**
     * 设置Papi全息图
     *
     * @param type     类型
     * @param location 生成位置
     */
    public static void createPapiHd(String type, Location location) {
        // 设置全息配置
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".enable", true, null, "/papi.yml");
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".world", Objects.requireNonNull(location.getWorld()).getName(), null, "/papi.yml");
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".x", location.getX(), null, "/papi.yml");
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".y", location.getY(), null, "/papi.yml");
        HandyConfigUtil.setPath(ConfigUtil.PAPI_CONFIG, type + ".z", location.getZ(), null, "/papi.yml");
        // 设置非必填配置
        HandyConfigUtil.setPathIsNotContains(ConfigUtil.PAPI_CONFIG, type + ".papi", "%" + type + "%", null, "/papi.yml");
        HandyConfigUtil.setPathIsNotContains(ConfigUtil.PAPI_CONFIG, type + ".line", 10, null, "/papi.yml");
        HandyConfigUtil.setPathIsNotContains(ConfigUtil.PAPI_CONFIG, type + ".material", "PAPER", null, "/papi.yml");
        HandyConfigUtil.setPathIsNotContains(ConfigUtil.PAPI_CONFIG, type + ".custom-model-data", 0, null, "/papi.yml");
        HandyConfigUtil.setPathIsNotContains(ConfigUtil.PAPI_CONFIG, type + ".title", "&e======" + type + "变量排行榜======", null, "/papi.yml");
        HandyConfigUtil.setPathIsNotContains(ConfigUtil.PAPI_CONFIG, type + ".lore", "&e${rank}   &f玩家:&e${player}   &f数量:&e${content}", null, "/papi.yml");
        HandyConfigUtil.setPathIsNotContains(ConfigUtil.PAPI_CONFIG, type + ".sort", "desc", null, "/papi.yml");
        // 重新读取配置
        ConfigUtil.PAPI_CONFIG = HandyConfigUtil.load("papi.yml");
    }

    /**
     * 删除全息
     *
     * @param type 类型
     */
    public static void deleteHd(String type) {
        String world = ConfigUtil.HD_CONFIG.getString(type + ".world", "");
        if (StrUtil.isEmpty(world)) {
            return;
        }
        double x = ConfigUtil.HD_CONFIG.getDouble(type + ".x");
        double y = ConfigUtil.HD_CONFIG.getDouble(type + ".y");
        double z = ConfigUtil.HD_CONFIG.getDouble(type + ".z");
        HdUtil.delete(new Location(Bukkit.getWorld(world), x, y, z));
    }

    /**
     * 删除Papi全息
     *
     * @param type 类型
     * @since 1.3.5
     */
    public static void deletePapiHd(String type) {
        String world = ConfigUtil.PAPI_CONFIG.getString(type + ".world", "");
        if (StrUtil.isEmpty(world)) {
            return;
        }
        double x = ConfigUtil.PAPI_CONFIG.getDouble(type + ".x");
        double y = ConfigUtil.PAPI_CONFIG.getDouble(type + ".y");
        double z = ConfigUtil.PAPI_CONFIG.getDouble(type + ".z");
        HdUtil.delete(new Location(Bukkit.getWorld(world), x, y, z));
    }

}
