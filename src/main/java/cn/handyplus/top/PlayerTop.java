package cn.handyplus.top;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.SqlManagerUtil;
import cn.handyplus.top.constants.TopConstants;
import cn.handyplus.top.hook.PlaceholderUtil;
import cn.handyplus.top.util.ConfigUtil;
import cn.handyplus.top.util.TopTaskUtil;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 主类
 *
 * @author handy
 */
public class PlayerTop extends JavaPlugin {
    private static PlayerTop INSTANCE;
    public static boolean USE_TITLE;
    public static boolean USE_TASK;
    public static boolean USE_RACE;
    public static boolean USE_GUILD;
    public static boolean USE_HOLOGRAPHIC_DISPLAYS;
    public static boolean USE_MC_MMO;
    public static Economy ECON;
    public static PlayerPoints PLAYER_POINTS;
    public static boolean USE_CMI;
    public static boolean USE_JOB;

    @Override
    public void onEnable() {
        INSTANCE = this;
        InitApi initApi = InitApi.getInstance(this);
        // 加载配置文件
        ConfigUtil.init();
        // 加载vault
        this.loadEconomy();
        // 加载PlayerPoints
        this.loadPlayerPoints();
        // 加载Placeholder
        this.loadPlaceholder();
        // 加载HolographicDisplays
        USE_HOLOGRAPHIC_DISPLAYS = BaseUtil.hook("HolographicDisplays", "HolographicDisplaysSucceedMsg", "HolographicDisplaysFailureMsg");
        // 加载PlayerTask
        USE_TASK = BaseUtil.hook("PlayerTask", "playerTaskSucceedMsg", "playerTaskFailureMsg");
        // 加载PlayerRace
        USE_RACE = BaseUtil.hook("PlayerRace", "playerRaceSucceedMsg", "playerRaceFailureMsg");
        // 加载PlayerTitle
        USE_TITLE = BaseUtil.hook("PlayerTitle", "playerTitleSucceedMsg", "playerTitleFailureMsg");
        // 加载PlayerGuild
        USE_GUILD = BaseUtil.hook("PlayerGuild", "playerGuildSucceedMsg", "playerGuildFailureMsg");
        // 加载McMmo
        USE_MC_MMO = BaseUtil.hook("mcMMO", "mcMMOSucceedMsg", "mcMMOFailureMsg");
        // 加载cmi
        USE_CMI = BaseUtil.hook("CMI", "cmiSucceedMsg", "cmiFailureMsg");
        // 加载jobs
        USE_JOB = BaseUtil.hook("Jobs", "jobsSucceedMsg", "jobsFailureMsg");

        initApi.initCommand("cn.handyplus.top.command")
                .initListener("cn.handyplus.top.listener")
                .enableSql("cn.handyplus.top.enter")
                .addMetrics(15377)
                .checkVersion(ConfigUtil.CONFIG.getBoolean(BaseConstants.IS_CHECK_UPDATE), TopConstants.PLUGIN_VERSION_URL);

        // 定时任务加载
        TopTaskUtil.init();

        MessageApi.sendConsoleMessage(ChatColor.GREEN + "已成功载入服务器！");
        MessageApi.sendConsoleMessage(ChatColor.GREEN + "Author:handy QQ群:1064982471");
    }

    @Override
    public void onDisable() {
        // 关闭数据源
        SqlManagerUtil.getInstance().close();
        MessageApi.sendConsoleMessage("§a已成功卸载！");
        MessageApi.sendConsoleMessage("§aAuthor:handy QQ群:1064982471");
    }

    public static PlayerTop getInstance() {
        return INSTANCE;
    }

    public static Economy getEconomy() {
        return ECON;
    }

    public static PlayerPoints getPlayerPoints() {
        return PLAYER_POINTS;
    }

    /**
     * 加载Vault
     */
    public void loadEconomy() {
        if (getServer().getPluginManager().getPlugin(BaseConstants.VAULT) == null) {
            MessageApi.sendConsoleMessage(BaseUtil.getLangMsg("vaultFailureMsg"));
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            MessageApi.sendConsoleMessage(BaseUtil.getLangMsg("vaultFailureMsg"));
            return;
        }
        ECON = rsp.getProvider();
        MessageApi.sendConsoleMessage(BaseUtil.getLangMsg("vaultSucceedMsg"));
    }

    /**
     * 加载Placeholder
     */
    public void loadPlaceholder() {
        if (Bukkit.getPluginManager().getPlugin(BaseConstants.PLACEHOLDER_API) != null) {
            new PlaceholderUtil(this).register();
            MessageApi.sendConsoleMessage(BaseUtil.getLangMsg("placeholderAPISucceedMsg"));
            return;
        }
        MessageApi.sendConsoleMessage(BaseUtil.getLangMsg("placeholderAPIFailureMsg"));
    }

    /**
     * 加载PlayerPoints
     */
    private void loadPlayerPoints() {
        if (Bukkit.getPluginManager().getPlugin(BaseConstants.PLAYER_POINTS) != null) {
            final Plugin plugin = this.getServer().getPluginManager().getPlugin(BaseConstants.PLAYER_POINTS);
            PLAYER_POINTS = (PlayerPoints) plugin;
            MessageApi.sendConsoleMessage(BaseUtil.getLangMsg("playerPointsSucceedMsg"));
            return;
        }
        MessageApi.sendConsoleMessage(BaseUtil.getLangMsg("playerPointsFailureMsg"));
    }

}