package cn.handyplus.top.hook;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.top.PlayerTop;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 全息图工具
 *
 * @author handy
 */
public class HolographicDisplaysUtil {
    private HolographicDisplaysUtil() {
    }

    protected static HolographicDisplaysUtil getInstance() {
        return HolographicDisplaysUtil.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final HolographicDisplaysUtil INSTANCE = new HolographicDisplaysUtil();
    }

    /**
     * 创建
     *
     * @param textLineList    内容
     * @param location        位置
     * @param material        材质
     * @param customModelData 模型
     */
    public void create(List<String> textLineList, Location location, String material, int customModelData) {
        if (!PlayerTop.USE_HOLOGRAPHIC_DISPLAYS) {
            return;
        }
        Hologram hologram = HologramsAPI.createHologram(PlayerTop.INSTANCE, location);
        // 内容
        hologram.clearLines();
        // 第一排的材质
        if (StrUtil.isNotEmpty(material)) {
            ItemStack itemStack = ItemStackUtil.getItemStack(material, null, null, false, customModelData);
            hologram.appendItemLine(itemStack);
        }
        for (String textLine : textLineList) {
            hologram.appendTextLine(BaseUtil.replaceChatColor(textLine));
        }
    }

    /**
     * 根据位置删除全息图
     *
     * @param location 位置
     */
    public void delete(Location location) {
        if (!PlayerTop.USE_HOLOGRAPHIC_DISPLAYS) {
            return;
        }
        // 删除旧的全息图
        for (Hologram hologram : HologramsAPI.getHolograms(PlayerTop.INSTANCE)) {
            Location hdLocation = hologram.getLocation();
            if (hdLocation.getX() == location.getX() && hdLocation.getY() == location.getY() && hdLocation.getZ() == location.getZ()) {
                hologram.delete();
                return;
            }
        }
    }

    /**
     * 删除全部全息图
     */
    public void deleteAll() {
        if (!PlayerTop.USE_HOLOGRAPHIC_DISPLAYS) {
            return;
        }
        // 删除旧的全息图
        for (Hologram hologram : HologramsAPI.getHolograms(PlayerTop.INSTANCE)) {
            hologram.delete();
        }
    }

}