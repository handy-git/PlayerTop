package cn.handyplus.top.hook;

import cn.handyplus.top.PlayerTop;
import org.bukkit.Location;

import java.util.List;

/**
 * 全息图处理
 *
 * @author handy
 */
public class HdUtil {

    /**
     * 创建
     *
     * @param textLineList 内容
     * @param location     位置
     */
    public static void create(List<String> textLineList, Location location, String materialName) {
        if (PlayerTop.USE_HOLOGRAPHIC_DISPLAYS) {
            CmiUtil.getInstance().create(textLineList, location, materialName);
            return;
        }
        if (PlayerTop.USE_CMI) {
            HolographicDisplaysUtil.getInstance().create(textLineList, location, materialName);
        }
    }

    /**
     * 根据位置删除全息图
     *
     * @param location 位置
     */
    public static void delete(Location location) {
        if (PlayerTop.USE_HOLOGRAPHIC_DISPLAYS) {
            CmiUtil.getInstance().delete(location);
            return;
        }
        if (PlayerTop.USE_CMI) {
            HolographicDisplaysUtil.getInstance().delete(location);
        }
    }

}
