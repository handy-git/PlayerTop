package com.handy.top.hook;

import com.handy.top.PlayerTop;

import java.util.UUID;

/**
 * 点券util
 *
 * @author handy
 */
public class PlayerPointsUtil {

    /**
     * 查询玩家点券
     *
     * @param playerUuid 玩家UUID
     * @return 玩家点券
     */
    public static int getPlayerPoints(UUID playerUuid) {
        if (PlayerTop.getPlayerPoints() == null || playerUuid == null) {
            return 0;
        }
        return PlayerTop.getPlayerPoints().getAPI().look(playerUuid);
    }

}