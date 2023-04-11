package cn.handyplus.top.util;

import cn.handyplus.lib.util.HandyConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

/**
 * 配置
 *
 * @author handy
 */
public class ConfigUtil {
    public static FileConfiguration CONFIG, LANG_CONFIG;
    public static FileConfiguration HD_CONFIG, FORMAT_CONFIG, PAPI_CONFIG;

    /**
     * 加载全部配置
     */
    public static void init() {
        // 加载config
        CONFIG = HandyConfigUtil.loadConfig();
        // 加载语言到jar
        LANG_CONFIG = HandyConfigUtil.loadLangConfig(CONFIG.getString("language"), false);
        // 全息配置
        HD_CONFIG = HandyConfigUtil.load("hologram.yml");
        // 全息配置
        FORMAT_CONFIG = HandyConfigUtil.load("format.yml");
        // 变量配置
        PAPI_CONFIG = HandyConfigUtil.load("papi.yml");
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
        // 1.0.9 添加語言文件
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "cmiSucceedMsg", "&a成功加载CMI,启用CMI全息图功能", null, "languages/" + CONFIG.getString("language") + ".yml");
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "cmiFailureMsg", "&4未找到CMI,未启用CMI全息图功能.", null, "languages/" + CONFIG.getString("language") + ".yml");
        // 1.1.1 添加語言文件
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "jobsSucceedMsg", "&a已成功加载Jobs 兼容Jobs功能.", null, "languages/" + CONFIG.getString("language") + ".yml");
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "jobsFailureMsg", "&7你的服务端没有安装Jobs 未兼容Jobs功能.", null, "languages/" + CONFIG.getString("language") + ".yml");
        LANG_CONFIG = HandyConfigUtil.loadLangConfig(CONFIG.getString("language"), false);

        // 1.1.8
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "format.playerGuildDonatedMoney", "&e${rank}   &f玩家:&e${player}   &f捐赠贡献:&e${content}", Arrays.asList("公会战捐赠贡献格式"), "format.yml");
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "hdFormat.playerGuildDonatedMoney.line", 10, null, "format.yml");
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "hdFormat.playerGuildDonatedMoney.material", "APPLE", null, "format.yml");
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "hdFormat.playerGuildDonatedMoney.title", "&e======全服公会捐赠贡献排行榜======", null, "format.yml");
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "hdFormat.playerGuildDonatedMoney.lore", "&e${rank}   &f玩家:&e${player}   &f捐赠贡献:&e${content}", null, "format.yml");
        FORMAT_CONFIG = HandyConfigUtil.load("format.yml");

    }

}