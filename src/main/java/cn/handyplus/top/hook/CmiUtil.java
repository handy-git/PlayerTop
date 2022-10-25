package cn.handyplus.top.hook;

import cn.handyplus.top.PlayerTop;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * CMI 工具
 *
 * @author handy
 */
public class CmiUtil {

    private CmiUtil() {
    }

    protected static CmiUtil getInstance() {
        return CmiUtil.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final CmiUtil INSTANCE = new CmiUtil();
    }

    /**
     * 全息图缓存
     */
    private final Map<Location, CMIHologram> CMI_HD_CACHE = new HashMap<>();

    /**
     * 创建
     *
     * @param textLineList 内容
     * @param location     位置
     */
    public void create(List<String> textLineList, Location location, String materialName) {
        if (!PlayerTop.USE_CMI) {
            return;
        }
        CMIHologram holo = new CMIHologram(UUID.randomUUID().toString(), location);
        holo.setLines(textLineList);
        CMI.getInstance().getHologramManager().addHologram(holo);
        holo.update();
        CMI_HD_CACHE.put(location, holo);
    }

    /**
     * 根据位置删除全息图
     *
     * @param location 位置
     */
    public void delete(Location location) {
        if (!PlayerTop.USE_CMI) {
            return;
        }
        // 删除旧的全息图
        CMIHologram cmiHologram = CMI_HD_CACHE.get(location);
        if (cmiHologram != null) {
            cmiHologram.remove();
            CMI_HD_CACHE.remove(location);
        }
    }

}