package cn.handyplus.top.hook;

import cn.handyplus.top.PlayerTop;
import org.bukkit.OfflinePlayer;

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
     * @param offlinePlayer 玩家
     * @return 玩家金币
     */
    public Integer getPlayerVault(OfflinePlayer offlinePlayer) {
        if (PlayerTop.getEconomy() == null) {
            return 0;
        }
        double dou = PlayerTop.getEconomy().getBalance(offlinePlayer);
        return (int) dou;
    }

}