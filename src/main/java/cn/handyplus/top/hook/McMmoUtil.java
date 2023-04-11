package cn.handyplus.top.hook;

import cn.handyplus.top.PlayerTop;
import com.gmail.nossr50.api.DatabaseAPI;
import com.gmail.nossr50.api.ExperienceAPI;

import java.util.UUID;

/**
 * McMmo工具
 *
 * @author handy
 */
public class McMmoUtil {

    private McMmoUtil() {
    }

    public static McMmoUtil getInstance() {
        return McMmoUtil.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final McMmoUtil INSTANCE = new McMmoUtil();
    }

    /**
     * 查询玩家技能等级
     *
     * @param playerUuid 玩家
     * @param type       类型
     * @return 技能等级
     * @since 1.0.2
     */
    public int getLevelOffline(UUID playerUuid, String type) {
        if (!PlayerTop.USE_MC_MMO || playerUuid == null) {
            return 0;
        }
        DatabaseAPI databaseApi = new DatabaseAPI();
        if (!databaseApi.doesPlayerExistInDB(playerUuid)) {
            return 0;
        }
        return ExperienceAPI.getLevelOffline(playerUuid, type);
    }

    /**
     * 查询玩家技能总等级
     *
     * @param playerUuid 玩家
     * @return 技能总等级
     * @since 1.0.2
     */
    public int getPowerLevelOffline(UUID playerUuid) {
        if (!PlayerTop.USE_MC_MMO || playerUuid == null) {
            return 0;
        }
        DatabaseAPI databaseApi = new DatabaseAPI();
        if (!databaseApi.doesPlayerExistInDB(playerUuid)) {
            return 0;
        }
        return ExperienceAPI.getPowerLevelOffline(playerUuid);
    }

}