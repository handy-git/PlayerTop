package com.handy.top.hook;

import com.handy.lib.core.StrUtil;
import com.handy.playertask.api.PlayerTaskApi;
import com.handy.top.PlayerTop;
import org.bukkit.entity.Player;

/**
 * 任务util
 *
 * @author handy
 */
public class PlayerTaskUtil {
    private PlayerTaskUtil() {
    }

    private static class SingletonHolder {
        private static final PlayerTaskUtil INSTANCE = new PlayerTaskUtil();
    }

    public static PlayerTaskUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 查询玩家任务币数量
     *
     * @param player 玩家
     * @return 玩家任务币
     */
    public static int getPlayerTaskNumber(Player player) {
        if (!PlayerTop.USE_TASK || player == null) {
            return 0;
        }
        Integer playerTaskCoin = PlayerTaskApi.getInstance().findAmountByPlayer(player);
        return playerTaskCoin != null ? playerTaskCoin : 0;
    }

    /**
     * 查询玩家任务币数量
     *
     * @param playerName 玩家名
     * @return 玩家任务币
     */
    public static int getPlayerTaskNumber(String playerName) {
        if (!PlayerTop.USE_TASK || StrUtil.isEmpty(playerName)) {
            return 0;
        }
        Integer playerTaskCoin = PlayerTaskApi.getInstance().findAmountByPlayer(playerName);
        return playerTaskCoin != null ? playerTaskCoin : 0;
    }

}