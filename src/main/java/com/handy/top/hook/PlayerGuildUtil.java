package com.handy.top.hook;

import com.handy.guild.api.PlayerGuildApi;
import com.handy.lib.core.StrUtil;
import com.handy.top.PlayerTop;
import org.bukkit.entity.Player;

/**
 * 公会util
 *
 * @author handy
 */
public class PlayerGuildUtil {

    private PlayerGuildUtil() {
    }

    public static PlayerGuildUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final PlayerGuildUtil INSTANCE = new PlayerGuildUtil();
    }

    /**
     * 查询玩家贡献数量
     *
     * @param player 玩家
     * @return 贡献数量
     */
    public static int getPlayerGuildMoney(Player player) {
        if (!PlayerTop.USE_GUILD || player == null) {
            return 0;
        }
        Integer playerGuildMoney = PlayerGuildApi.getInstance().getPlayerMoney(player);
        return playerGuildMoney != null ? playerGuildMoney : 0;
    }

    /**
     * 查询玩家贡献数量
     *
     * @param playerName 玩家名
     * @return 贡献数量
     */
    public static int getPlayerGuildMoney(String playerName) {
        if (!PlayerTop.USE_GUILD || StrUtil.isEmpty(playerName)) {
            return 0;
        }
        Integer playerGuildMoney = PlayerGuildApi.getInstance().getPlayerMoney(playerName);
        return playerGuildMoney != null ? playerGuildMoney : 0;
    }

}