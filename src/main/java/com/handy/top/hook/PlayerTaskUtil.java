package com.handy.top.hook;

import com.handy.lib.core.StrUtil;
import com.handy.playertask.api.PlayerTaskApi;
import com.handy.top.PlayerTop;

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
     * @param playerName 玩家名
     * @return 玩家任务币
     */
    public int getPlayerTaskCoin(String playerName) {
        if (!PlayerTop.USE_TASK || StrUtil.isEmpty(playerName)) {
            return 0;
        }
        Integer playerTaskCoin = PlayerTaskApi.getInstance().findAmountByPlayer(playerName);
        return playerTaskCoin != null ? playerTaskCoin : 0;
    }

}