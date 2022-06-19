package com.handy.top.hook;

import com.handy.lib.core.StrUtil;
import com.handy.top.PlayerTop;

/**
 * 金币util
 *
 * @author handy
 */
public class VaultUtil {

    private VaultUtil() {
    }

    public static VaultUtil getInstance() {
        return VaultUtil.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final VaultUtil INSTANCE = new VaultUtil();
    }

    /**
     * 查询玩家金币
     *
     * @param playerName 玩家名
     * @return 玩家金币
     */
    public double getPlayerVault(String playerName) {
        if (PlayerTop.getEconomy() == null || StrUtil.isEmpty(playerName)) {
            return 0.0;
        }
        return PlayerTop.getEconomy().getBalance(playerName);
    }

}