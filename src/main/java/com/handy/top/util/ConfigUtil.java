package com.handy.top.util;

import com.handy.lib.api.LangMsgApi;
import com.handy.lib.util.HandyConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * 配置
 *
 * @author handy
 */
public class ConfigUtil {
    public static FileConfiguration CONFIG, LANG_CONFIG;
    public static FileConfiguration HD_CONFIG;

    /**
     * 加载全部配置
     */
    public static void init() {
        // 加载config
        CONFIG = HandyConfigUtil.loadConfig();
        // 加载语言到jar
        LANG_CONFIG = HandyConfigUtil.load("languages/" + CONFIG.getString("language") + ".yml");
        LangMsgApi.initLangMsg(LANG_CONFIG);
        // 全息配置
        HD_CONFIG = HandyConfigUtil.load("hologram.yml");
    }

}