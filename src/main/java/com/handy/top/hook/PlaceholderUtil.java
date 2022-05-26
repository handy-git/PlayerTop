package com.handy.top.hook;

import com.handy.lib.core.CollUtil;
import com.handy.top.PlayerTop;
import com.handy.top.constants.PlayerTopTypeEnum;
import com.handy.top.enter.TopPlayer;
import com.handy.top.service.TopPlayerService;
import com.handy.top.util.ConfigUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.List;

/**
 * 变量扩展
 *
 * @author handy
 */
public class PlaceholderUtil extends PlaceholderExpansion {
    private final PlayerTop plugin;

    public PlaceholderUtil(PlayerTop plugin) {
        this.plugin = plugin;
    }

    /**
     * 变量前缀
     *
     * @return 结果
     */
    @Override
    public String getIdentifier() {
        return "playerTop";
    }

    /**
     * 注册变量
     *
     * @param player      玩家
     * @param placeholder 变量字符串
     * @return 变量
     */
    @Override
    public String onRequest(OfflinePlayer player, String placeholder) {
        if (player == null) {
            return null;
        }
        String[] placeholderStr = placeholder.split("_");
        if (placeholderStr.length < 2) {
            return "";
        }
        String type = placeholderStr[0];
        int pageNum = Integer.parseInt(placeholderStr[1]);

        PlayerTopTypeEnum topTypeEnum = PlayerTopTypeEnum.getType(type);
        if (topTypeEnum == null) {
            return "";
        }
        // 查询对应记录
        List<TopPlayer> list = TopPlayerService.getInstance().page(topTypeEnum, pageNum, 1);

        String content = "";
        if (CollUtil.isNotEmpty(list)) {
            String format = "";
            TopPlayer enter = list.get(0);
            switch (topTypeEnum) {
                case VAULT:
                    format = ConfigUtil.CONFIG.getString("format.vault", "");
                    content = format.replace("${player}", enter.getPlayerName()).replace("${content}", enter.getVault().intValue() + "").replace("${original_content}", enter.getVault() + "");
                    break;
                case PLAYER_POINTS:
                    format = ConfigUtil.CONFIG.getString("format.player_points", "");
                    content = format.replace("${player}", enter.getPlayerName()).replace("${content}", enter.getPlayerPoints() + "").replace("${original_content}", enter.getPlayerPoints() + "");
                    break;
                case PLAYER_TITLE_COIN:
                    format = ConfigUtil.CONFIG.getString("format.player_title_coin", "");
                    content = format.replace("${player}", enter.getPlayerName()).replace("${content}", enter.getPlayerTitleCoin() + "").replace("${original_content}", enter.getPlayerTitleCoin() + "");
                    break;
                case PLAYER_TITLE_NUMBER:
                    format = ConfigUtil.CONFIG.getString("format.player_title_number", "");
                    content = format.replace("${player}", enter.getPlayerName()).replace("${content}", enter.getPlayerTitleNumber() + "").replace("${original_content}", enter.getPlayerTitleNumber() + "");
                    break;
                case PLAYER_TASK_COIN:
                    format = ConfigUtil.CONFIG.getString("format.player_task_coin", "");
                    content = format.replace("${player}", enter.getPlayerName()).replace("${content}", enter.getPlayerTaskCoin() + "").replace("${original_content}", enter.getPlayerTaskCoin() + "");
                    break;
                case PLAYER_GUILD_MONEY:
                    format = ConfigUtil.CONFIG.getString("format.player_guild_money", "");
                    content = format.replace("${player}", enter.getPlayerName()).replace("${content}", enter.getPlayerGuildMoney() + "").replace("${original_content}", enter.getPlayerGuildMoney() + "");
                    break;
                default:
                    break;
            }
        }
        return plugin.getConfig().getString(placeholder, content);
    }

    /**
     * 因为这是一个内部类，
     * 你必须重写这个方法，让PlaceholderAPI知道不要注销你的扩展类
     *
     * @return 结果
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * 因为这是一个内部类，所以不需要进行这种检查
     * 我们可以简单地返回{@code true}
     *
     * @return 结果
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * 作者
     *
     * @return 结果
     */
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * 版本
     *
     * @return 结果
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}
