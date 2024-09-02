package cn.handyplus.top.util;

import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.Map;

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
        LANG_CONFIG = HandyConfigUtil.loadLangConfig(CONFIG.getString("language"));
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
        // 1.1.1 添加語言文件
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "jobsSucceedMsg", "&a已成功加载Jobs 兼容Jobs功能.", null, "languages/" + CONFIG.getString("language") + ".yml");
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "jobsFailureMsg", "&7你的服务端没有安装Jobs 未兼容Jobs功能.", null, "languages/" + CONFIG.getString("language") + ".yml");
        // 1.3.2 添加没有世界的提醒
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "noWorld", "&c生成全息排行榜没有找到对应世界:${world}", null, "languages/" + CONFIG.getString("language") + ".yml");
        // 1.4.1 添加提醒
        HandyConfigUtil.setPathIsNotContains(LANG_CONFIG, "hdFailureMsg", "&a没有找到可使用的全息图插件", null, "languages/" + CONFIG.getString("language") + ".yml");
        LANG_CONFIG = HandyConfigUtil.loadLangConfig(CONFIG.getString("language"));

        // 1.1.8
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "format.playerGuildDonatedMoney", "&e${rank}   &f玩家:&e${player}   &f捐赠贡献:&e${content}", Collections.singletonList("公会战捐赠贡献格式"), "format.yml");
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "hdFormat.playerGuildDonatedMoney.line", 10, null, "format.yml");
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "hdFormat.playerGuildDonatedMoney.material", "APPLE", null, "format.yml");
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "hdFormat.playerGuildDonatedMoney.title", "&e======全服公会捐赠贡献排行榜======", null, "format.yml");
        HandyConfigUtil.setPathIsNotContains(FORMAT_CONFIG, "hdFormat.playerGuildDonatedMoney.lore", "&e${rank}   &f玩家:&e${player}   &f捐赠贡献:&e${content}", null, "format.yml");
        FORMAT_CONFIG = HandyConfigUtil.load("format.yml");

        // 1.2.2 添加配置
        for (String type : PlayerTopTypeEnum.getTypeList()) {
            HandyConfigUtil.setPathIsNotContains(CONFIG, "enable." + type, true, null, "config.yml");
        }
        // 1.2.8 添加黑名单
        HandyConfigUtil.setPathIsNotContains(CONFIG, "blacklist", Collections.singletonList("md5"), Collections.singletonList("黑名单,该名单内的玩家会被过滤掉"), "config.yml");
        // 1.3.2 添加过滤值
        HandyConfigUtil.setPathIsNotContains(CONFIG, "filter", Collections.singletonList(""), Collections.singletonList("papi需要过滤的值(只能为数字) 如果变量获取到这个值就会过滤掉"), "config.yml");
        // 1.4.3 添加初始化
        HandyConfigUtil.setPathIsNotContains(CONFIG, "isInit", false, Collections.singletonList("是否自动启动服务器时候初始化一次数据(获取全部玩家),之后只刷新在线玩家的排行数据"), "config.yml");
        // 1.4.8 添加弩/三叉戟/重锤
        HandyConfigUtil.setPathIsNotContains(CONFIG, "enable.mcMmoCrossbows", false, null, "config.yml");
        HandyConfigUtil.setPathIsNotContains(CONFIG, "enable.mcMmoMaces", false, null, "config.yml");
        HandyConfigUtil.setPathIsNotContains(CONFIG, "enable.mcMmoTridents", false, null, "config.yml");
        // 1.5.0 添加同步数据模式配置
        HandyConfigUtil.setPathIsNotContains(CONFIG, "syncMode", "online", Collections.singletonList("同步数据模式 online 是只同步在线玩家  offline 是同步全部玩家(包含不在线)"), "config.yml");
        CONFIG = HandyConfigUtil.loadConfig();
    }

    /**
     * 获取Papi一级目录
     *
     * @return 一级目录
     * @since 1.3.5
     */
    public static Map<String, Object> getPapiOneChildMap() {
        return ConfigUtil.PAPI_CONFIG.getValues(false);
    }

}