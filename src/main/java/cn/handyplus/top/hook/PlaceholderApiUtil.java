package cn.handyplus.top.hook;

import cn.handyplus.top.PlayerTop;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 变量工具类
 *
 * @author handy
 */
public class PlaceholderApiUtil {

    /**
     * 替换变量
     *
     * @param player 玩家
     * @param str    字符串
     * @return 新字符串
     */
    public static String set(Player player, String str) {
        if (PlayerTop.USE_PAPI && player != null) {
            return PlaceholderAPI.setPlaceholders(player, str);
        }
        return str;
    }

    /**
     * 替换变量
     *
     * @param playerName 玩家
     * @param str        字符串
     * @return 新字符串
     */
    public static String set(String playerName, String str) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (PlayerTop.USE_PAPI) {
            return PlaceholderAPI.setPlaceholders(offlinePlayer, str);
        }
        return str;
    }

    /**
     * 替换变量
     *
     * @param player  玩家
     * @param strList 字符串集合
     * @return 新字符串集合
     */
    public static List<String> set(Player player, List<String> strList) {
        if (PlayerTop.USE_PAPI && player != null) {
            return PlaceholderAPI.setPlaceholders(player, strList);
        }
        return strList;
    }

}