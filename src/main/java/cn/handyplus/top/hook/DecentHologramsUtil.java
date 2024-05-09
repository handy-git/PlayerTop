package cn.handyplus.top.hook;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.top.PlayerTop;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DecentHolograms 全息图处理
 * <a href="https://www.spigotmc.org/resources/96927">DecentHolograms</a>
 *
 * @author handy
 */
public class DecentHologramsUtil {
    private DecentHologramsUtil() {
    }

    protected static DecentHologramsUtil getInstance() {
        return DecentHologramsUtil.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final DecentHologramsUtil INSTANCE = new DecentHologramsUtil();
    }

    /**
     * 全息图缓存
     */
    private final Map<Location, String> DECENT_HOLOGRAMS_HD_CACHE = new HashMap<>();

    /**
     * 创建
     *
     * @param textLineList 内容
     * @param location     位置
     */
    public void create(List<String> textLineList, Location location) {
        if (!PlayerTop.USE_DECENT_HOLOGRAMS) {
            return;
        }
        String name = UUID.randomUUID().toString();
        DHAPI.createHologram(name, location, textLineList);
        DECENT_HOLOGRAMS_HD_CACHE.put(location, name);
    }

    /**
     * 根据位置删除全息图
     *
     * @param location 位置
     */
    public void delete(Location location) {
        if (!PlayerTop.USE_DECENT_HOLOGRAMS) {
            return;
        }
        String name = DECENT_HOLOGRAMS_HD_CACHE.get(location);
        if (StrUtil.isEmpty(name)) {
            return;
        }
        DHAPI.removeHologram(name);
        DECENT_HOLOGRAMS_HD_CACHE.remove(location);
    }

    /**
     * 删除全部全息图
     */
    public void deleteAll() {
        if (!PlayerTop.USE_DECENT_HOLOGRAMS) {
            return;
        }
        // 删除旧的全息图
        for (Location location : DECENT_HOLOGRAMS_HD_CACHE.keySet()) {
            String name = DECENT_HOLOGRAMS_HD_CACHE.get(location);
            if (StrUtil.isNotEmpty(name)) {
                DHAPI.removeHologram(name);
            }
        }
        DECENT_HOLOGRAMS_HD_CACHE.clear();
    }

}
