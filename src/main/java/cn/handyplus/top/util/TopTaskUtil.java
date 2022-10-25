package cn.handyplus.top.util;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.enter.TopPlayer;
import cn.handyplus.top.hook.*;
import cn.handyplus.top.service.TopPlayerService;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.MemorySection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * 定时任务
 *
 * @author handy
 */
public class TopTaskUtil {
    private static final Semaphore TASK_LOCK = new Semaphore(1);

    /**
     * 初始化定时任务
     */
    public static void init() {
        // 初始化全息图
        createHd();
        // 定时任务获取数据
        long task = ConfigUtil.CONFIG.getLong("task", 300);
        if (task > 0) {
            getDataTask();
        }
    }

    /**
     * 定时获取数据
     */
    private static void getDataTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!TASK_LOCK.tryAcquire()) {
                    return;
                }
                try {
                    // 执行
                    execute();
                } finally {
                    TASK_LOCK.release();
                }
            }
        }.runTaskTimerAsynchronously(PlayerTop.getInstance(), 20 * 30, ConfigUtil.CONFIG.getLong("task", 300) * 20);
    }

    /**
     * 同步方法
     */
    public static void execute() {
        long start = System.currentTimeMillis();
        // 全部玩家
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            String playerName = offlinePlayer.getName();
            if (StrUtil.isEmpty(playerName)) {
                continue;
            }
            // 构建数据
            TopPlayer topPlayer = new TopPlayer();
            topPlayer.setPlayerName(playerName);
            topPlayer.setPlayerUuid(offlinePlayer.getUniqueId().toString());
            topPlayer.setOp(false);
            if (ConfigUtil.CONFIG.getBoolean("isOp")) {
                topPlayer.setOp(offlinePlayer.isOp());
            }
            // 金币
            topPlayer.setVault(VaultUtil.getInstance().getPlayerVault(playerName));
            // 点券
            topPlayer.setPlayerPoints(PlayerPointsUtil.getInstance().getPlayerPoints(offlinePlayer.getUniqueId()));
            // 称号币
            topPlayer.setPlayerTitleCoin(PlayerTitleUtil.getInstance().getPlayerTitleCoin(playerName));
            // 称号数量
            topPlayer.setPlayerTitleNumber(PlayerTitleUtil.getInstance().getPlayerTitleNumber(playerName));
            // 任务币数量
            topPlayer.setPlayerTaskCoin(PlayerTaskUtil.getInstance().getPlayerTaskCoin(playerName));
            // 公会贡献数量和kd
            if (PlayerTop.USE_GUILD) {
                topPlayer.setPlayerGuildMoney(PlayerGuildUtil.getInstance().getPlayerGuildMoney(playerName));
                topPlayer.setPlayerGuildKill(PlayerGuildUtil.getInstance().getPlayerGuildKill(playerName));
                topPlayer.setPlayerGuildDie(PlayerGuildUtil.getInstance().getPlayerGuildDie(playerName));
            }
            // McMmo
            if (PlayerTop.USE_MC_MMO) {
                topPlayer.setMcMmoSum(McMmoUtil.getInstance().getPowerLevelOffline(offlinePlayer.getUniqueId()));
                topPlayer.setMcMmoAxes(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.AXES.name()));
                topPlayer.setMcMmoMining(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.MINING.name()));
                topPlayer.setMcMmoRepair(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.REPAIR.name()));
                topPlayer.setMcMmoTaming(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.TAMING.name()));
                topPlayer.setMcMmoSwords(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.SWORDS.name()));
                topPlayer.setMcMmoAlchemy(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.ALCHEMY.name()));
                topPlayer.setMcMmoArchery(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.ARCHERY.name()));
                topPlayer.setMcMmoFishing(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.FISHING.name()));
                topPlayer.setMcMmoSalvage(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.SALVAGE.name()));
                topPlayer.setMcMmoUnarmed(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.UNARMED.name()));
                topPlayer.setMcMmoSmelting(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.SMELTING.name()));
                topPlayer.setMcMmoHerbalism(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.HERBALISM.name()));
                topPlayer.setMcMmoAcrobatics(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.ACROBATICS.name()));
                topPlayer.setMcMmoExcavation(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.EXCAVATION.name()));
                topPlayer.setMcMmoWoodcutting(McMmoUtil.getInstance().getLevelOffline(playerName, PrimarySkillType.WOODCUTTING.name()));
            }
            TopPlayerService.getInstance().saveOrUpdate(topPlayer);
        }
        MessageApi.sendConsoleDebugMessage("同步" + offlinePlayers.length + "位玩家" + ",消耗ms:" + (System.currentTimeMillis() - start));
        // 刷新排行榜
        Bukkit.getScheduler().runTask(PlayerTop.getInstance(), TopTaskUtil::createHd);
    }

    /**
     * 初始化全息图
     */
    public static void createHd() {
        // 一级目录
        Map<String, Object> values = ConfigUtil.HD_CONFIG.getValues(false);
        for (String type : values.keySet()) {
            // 二级目录
            MemorySection memorySection = (MemorySection) values.get(type);
            if (memorySection == null) {
                continue;
            }
            String world = ConfigUtil.HD_CONFIG.getString(type + ".world", "");
            double x = ConfigUtil.HD_CONFIG.getDouble(type + ".x");
            double y = ConfigUtil.HD_CONFIG.getDouble(type + ".y");
            double z = ConfigUtil.HD_CONFIG.getDouble(type + ".z");
            // 获取类型
            PlayerTopTypeEnum playerTopTypeEnum = PlayerTopTypeEnum.getType(type);
            if (playerTopTypeEnum == null) {
                continue;
            }
            Location location = new Location(Bukkit.getWorld(world), x, y, z);

            // 先进行删除
            HdUtil.delete(location);
            // 判断是否开启状态
            boolean enable = memorySection.getBoolean("enable");
            if (!enable) {
                continue;
            }
            // 新增全息图
            TopUtil.createHd(playerTopTypeEnum, location);
        }
    }

}