package cn.handyplus.top.util;

import cn.handyplus.lib.api.LangMsgApi;
import cn.handyplus.lib.util.HandyConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * 配置
 *
 * @author handy
 */
public class ConfigUtil {
    public static FileConfiguration CONFIG, LANG_CONFIG;
    public static FileConfiguration HD_CONFIG, FORMAT_CONFIG;

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
        // 全息配置
        FORMAT_CONFIG = HandyConfigUtil.load("format.yml");
        // 升级配置
        upConfig();
    }

    /**
     * 升级节点处理
     *
     * @since 1.0.2
     */
    public static void upConfig() {
        // 1.0.2 添加語言文件
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "mcMMOSucceedMsg", "&a已成功加载mcMMO 兼容mcMMO功能.", null, "languages/" + CONFIG.getString("language") + ".yml");
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "mcMMOFailureMsg", "&7你的服务端没有安装mcMMO 未兼容mcMMO功能.", null, "languages/" + CONFIG.getString("language") + ".yml");
        LANG_CONFIG = HandyConfigUtil.load("languages/" + CONFIG.getString("language") + ".yml");
    }

}