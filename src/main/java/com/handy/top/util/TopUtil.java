package com.handy.top.util;

import com.handy.lib.core.CollUtil;
import com.handy.lib.core.StrUtil;
import com.handy.lib.util.HandyConfigUtil;
import com.handy.top.constants.PlayerTopTypeEnum;
import com.handy.top.enter.TopPlayer;
import com.handy.top.hook.HolographicDisplaysUtil;
import com.handy.top.service.TopPlayerService;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author handy
 */
public class TopUtil {

    /**
     * 根据类型获取模板
     *
     * @param topTypeEnum 类型
     * @param newLore     替换的lore
     * @param topPlayer   数据
     * @return 新值
     * @since 1.0.3
     */
    public static String getContent(PlayerTopTypeEnum topTypeEnum, String newLore, TopPlayer topPlayer) {
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
            case MC_MMO:
                content = newLore.replace("${content}", topPlayer.getMcMmoSum() + "").replace("${original_content}", topPlayer.getMcMmoSum() + "");
                break;
            case MC_MMO_AXES:
                content = newLore.replace("${content}", topPlayer.getMcMmoAxes() + "").replace("${original_content}", topPlayer.getMcMmoAxes() + "");
                break;
            case MC_MMO_MINING:
                content = newLore.replace("${content}", topPlayer.getMcMmoMining() + "").replace("${original_content}", topPlayer.getMcMmoMining() + "");
                break;
            case MC_MMO_REPAIR:
                content = newLore.replace("${content}", topPlayer.getMcMmoRepair() + "").replace("${original_content}", topPlayer.getMcMmoRepair() + "");
                break;
            case MC_MMO_SWORDS:
                content = newLore.replace("${content}", topPlayer.getMcMmoSwords() + "").replace("${original_content}", topPlayer.getMcMmoSwords() + "");
                break;
            case MC_MMO_TAMING:
                content = newLore.replace("${content}", topPlayer.getMcMmoTaming() + "").replace("${original_content}", topPlayer.getMcMmoTaming() + "");
                break;
            case MC_MMO_ALCHEMY:
                content = newLore.replace("${content}", topPlayer.getMcMmoAlchemy() + "").replace("${original_content}", topPlayer.getMcMmoAlchemy() + "");
                break;
            case MC_MMO_ARCHERY:
                content = newLore.replace("${content}", topPlayer.getMcMmoArchery() + "").replace("${original_content}", topPlayer.getMcMmoArchery() + "");
                break;
            case MC_MMO_FISHING:
                content = newLore.replace("${content}", topPlayer.getMcMmoFishing() + "").replace("${original_content}", topPlayer.getMcMmoFishing() + "");
                break;
            case MC_MMO_SALVAGE:
                content = newLore.replace("${content}", topPlayer.getMcMmoSalvage() + "").replace("${original_content}", topPlayer.getMcMmoSalvage() + "");
                break;
            case MC_MMO_UNARMED:
                content = newLore.replace("${content}", topPlayer.getMcMmoUnarmed() + "").replace("${original_content}", topPlayer.getMcMmoUnarmed() + "");
                break;
            case MC_MMO_SMELTING:
                content = newLore.replace("${content}", topPlayer.getMcMmoSmelting() + "").replace("${original_content}", topPlayer.getMcMmoSmelting() + "");
                break;
            case MC_MMO_HERBALISM:
                content = newLore.replace("${content}", topPlayer.getMcMmoHerbalism() + "").replace("${original_content}", topPlayer.getMcMmoHerbalism() + "");
                break;
            case MC_MMO_ACROBATICS:
                content = newLore.replace("${content}", topPlayer.getMcMmoAcrobatics() + "").replace("${original_content}", topPlayer.getMcMmoAcrobatics() + "");
                break;
            case MC_MMO_EXCAVATION:
                content = newLore.replace("${content}", topPlayer.getMcMmoExcavation() + "").replace("${original_content}", topPlayer.getMcMmoExcavation() + "");
                break;
            case MC_MMO_WOODCUTTING:
                content = newLore.replace("${content}", topPlayer.getMcMmoWoodcutting() + "").replace("${original_content}", topPlayer.getMcMmoWoodcutting() + "");
                break;
            case PLAYER_GUILD_KILL:
                content = newLore.replace("${content}", topPlayer.getPlayerGuildKill() + "").replace("${original_content}", topPlayer.getPlayerGuildKill() + "");
                break;
            case PLAYER_GUILD_DIE:
                content = newLore.replace("${content}", topPlayer.getPlayerGuildDie() + "").replace("${original_content}", topPlayer.getPlayerGuildDie() + "");
                break;
            default:
                content = newLore;
                break;
        }
        return content;
    }

    /**
     * 生成全息图
     *
     * @param topTypeEnum 类型
     * @param location    生成位置
     */
    public static void createHd(PlayerTopTypeEnum topTypeEnum, Location location) {
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
        List<TopPlayer> topPlayerList = TopPlayerService.getInstance().page(topTypeEnum, 1, line);
        List<String> textLineList = new ArrayList<>();
        if (StrUtil.isNotEmpty(title)) {
            textLineList.add(title);
        }
        // 判断有数据 进行构建行
        if (CollUtil.isNotEmpty(topPlayerList)) {
            for (int i = 0; i < topPlayerList.size(); i++) {
                TopPlayer topPlayer = topPlayerList.get(i);
                String newLore = lore.replace("${player}", topPlayer.getPlayerName()).replace("${rank}", (i + 1) + "");
                textLineList.add(getContent(topTypeEnum, newLore, topPlayer));
            }
        }
        // 创建全息
        HolographicDisplaysUtil.getInstance().create(textLineList, location, material);
        // 保存全息配置
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".enable", true, Collections.singletonList("是否开启"), "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".world", Objects.requireNonNull(location.getWorld()).getName(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".x", location.getX(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".y", location.getY(), null, "/hologram.yml");
        HandyConfigUtil.setPath(ConfigUtil.HD_CONFIG, type + ".z", location.getZ(), null, "/hologram.yml");
        ConfigUtil.HD_CONFIG = HandyConfigUtil.load("hologram.yml");
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
        HolographicDisplaysUtil.getInstance().delete(new Location(Bukkit.getWorld(world), x, y, z));
    }

}
