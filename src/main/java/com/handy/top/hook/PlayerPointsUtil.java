package com.handy.top.hook;

import com.handy.top.PlayerTop;

import java.util.UUID;

/**
 * 点券util
 *
 * @author handy
 */
public class PlayerPointsUtil {

    private PlayerPointsUtil() {
    }

    public static PlayerPointsUtil getInstance() {
        return PlayerPointsUtil.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final PlayerPointsUtil INSTANCE = new PlayerPointsUtil();
    }

    /**
     * 查询玩家点券
     *
     * @param playerUuid 玩家UUID
     * @return 玩家点券
     */
    public int getPlayerPoints(UUID playerUuid) {
        if (PlayerTop.getPlayerPoints() == null || playerUuid == null) {
            return 0;
        }
        return PlayerTop.getPlayerPoints().getAPI().look(playerUuid);
    }

}