package cn.handyplus.top.hook;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.top.PlayerTop;
import com.handy.playertitle.api.PlayerTitleApi;

/**
 * 称号util
 *
 * @author handy
 */
public class PlayerTitleUtil {

    private PlayerTitleUtil() {
    }

    public static PlayerTitleUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final PlayerTitleUtil INSTANCE = new PlayerTitleUtil();
    }

    /**
     * 查询玩家称号数量
     *
     * @param playerName 玩家名
     * @return 称号数量
     */
    public long getPlayerTitleNumber(String playerName) {
        if (!PlayerTop.USE_TITLE || StrUtil.isEmpty(playerName)) {
            return 0;
        }
        Integer playerTitleNum = PlayerTitleApi.getInstance().getPlayerTitleNum(playerName);
        return playerTitleNum != null ? playerTitleNum : 0L;
    }

    /**
     * 查询玩家称号币
     *
     * @param playerName 玩家名
     * @return 称号币数量
     */
    public long getPlayerTitleCoin(String playerName) {
        if (!PlayerTop.USE_TITLE || StrUtil.isEmpty(playerName)) {
            return 0L;
        }
        Long playerCoinNum = PlayerTitleApi.getInstance().getPlayerCoinNum(playerName);
        return playerCoinNum != null ? playerCoinNum : 0L;
    }

}