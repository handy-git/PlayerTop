package com.handy.top.hook;

import com.handy.guild.api.PlayerGuildApi;
import com.handy.lib.core.StrUtil;
import com.handy.top.PlayerTop;

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
     * @param playerName 玩家名
     * @return 贡献数量
     */
    public int getPlayerGuildMoney(String playerName) {
        if (!PlayerTop.USE_GUILD || StrUtil.isEmpty(playerName)) {
            return 0;
        }
        Integer playerGuildMoney = PlayerGuildApi.getInstance().getPlayerMoney(playerName);
        return playerGuildMoney != null ? playerGuildMoney : 0;
    }

    /**
     * 查询玩家公会战击杀
     *
     * @param playerName 玩家名
     * @return 公会战击杀
     */
    public int getPlayerGuildKill(String playerName) {
        if (!PlayerTop.USE_GUILD || StrUtil.isEmpty(playerName)) {
            return 0;
        }
        Integer playerGuildKill = PlayerGuildApi.getInstance().getPlayerKill(playerName);
        return playerGuildKill != null ? playerGuildKill : 0;
    }

    /**
     * 查询玩家公会战死亡
     *
     * @param playerName 玩家名
     * @return 公会战死亡
     */
    public int getPlayerGuildDie(String playerName) {
        if (!PlayerTop.USE_GUILD || StrUtil.isEmpty(playerName)) {
            return 0;
        }
        Integer playerGuildDie = PlayerGuildApi.getInstance().getPlayerDie(playerName);
        return playerGuildDie != null ? playerGuildDie : 0;
    }

}