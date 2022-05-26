package com.handy.top.hook;

import com.handy.lib.core.StrUtil;
import com.handy.top.PlayerTop;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * 金币util
 *
 * @author handy
 */
public class VaultUtil {

    /**
     * 查询玩家金币
     *
     * @param offlinePlayer 玩家
     * @return 玩家金币
     */
    public static double getPlayerVault(OfflinePlayer offlinePlayer) {
        if (PlayerTop.getEconomy() == null || offlinePlayer == null) {
            return 0.0;
        }
        return PlayerTop.getEconomy().getBalance(offlinePlayer);
    }

    /**
     * 查询玩家金币
     *
     * @param player 玩家
     * @return 玩家金币
     */
    public static double getPlayerVault(Player player) {
        if (PlayerTop.getEconomy() == null || player == null) {
            return 0.0;
        }
        return PlayerTop.getEconomy().getBalance(player);
    }

    /**
     * 查询玩家金币
     *
     * @param playerName 玩家名
     * @return 玩家金币
     */
    public static double getPlayerVault(String playerName) {
        if (PlayerTop.getEconomy() == null || StrUtil.isEmpty(playerName)) {
            return 0.0;
        }
        return PlayerTop.getEconomy().getBalance(playerName);
    }

}