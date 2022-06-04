package com.handy.top.task;

import com.handy.guild.PlayerGuild;
import com.handy.lib.api.MessageApi;
import com.handy.lib.core.StrUtil;
import com.handy.top.PlayerTop;
import com.handy.top.command.admin.CreateHdCommand;
import com.handy.top.constants.PlayerTopTypeEnum;
import com.handy.top.hook.*;
import com.handy.top.service.TopPlayerService;
import com.handy.top.util.ConfigUtil;
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
public class TopTask {
    private static final Semaphore VAULT_LOCK = new Semaphore(1);
    private static final Semaphore PLAYER_POINTS_LOCK = new Semaphore(1);
    private static final Semaphore PLAYER_TITLE_NUMBER_LOCK = new Semaphore(1);
    private static final Semaphore PLAYER_TITLE_COIN_LOCK = new Semaphore(1);
    private static final Semaphore PLAYER_TASK_COIN_LOCK = new Semaphore(1);
    private static final Semaphore PLAYER_GUILD_MONEY_LOCK = new Semaphore(1);

    /**
     * 初始化定时任务
     */
    public static void init() {
        createHd();
        vaultTask();
        playerPointsTask();
        playerTitleNumberTask();
        playerTitleCoinTask();
        playerTaskCoinTask();
        playerGuildMoneyTask();
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
            HolographicDisplaysUtil.delete(location);
            // 判断是否开启状态
            boolean enable = memorySection.getBoolean("enable");
            if (!enable) {
                continue;
            }
            // 新增全息图
            CreateHdCommand.createHd(playerTopTypeEnum, location);
        }
    }

    /**
     * 刷新全息图
     *
     * @param playerTopTypeEnum 类型
     */
    public static void refreshHd(PlayerTopTypeEnum playerTopTypeEnum) {
        String type = playerTopTypeEnum.getType();
        // 判断是否开启
        boolean enable = ConfigUtil.HD_CONFIG.getBoolean(type + ".enable");
        if (!enable) {
            return;
        }
        String world = ConfigUtil.HD_CONFIG.getString(type + ".world", "");
        double x = ConfigUtil.HD_CONFIG.getDouble(type + ".x");
        double y = ConfigUtil.HD_CONFIG.getDouble(type + ".y");
        double z = ConfigUtil.HD_CONFIG.getDouble(type + ".z");
        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        // 先进行删除
        HolographicDisplaysUtil.delete(location);
        // 新增全息图
        CreateHdCommand.createHd(playerTopTypeEnum, location);
    }

    /**
     * 玩家金币
     */
    private static void vaultTask() {
        // 未加载到插件,定时任务不启动
        if (PlayerTop.getEconomy() == null) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!VAULT_LOCK.tryAcquire()) {
                    return;
                }
                try {
                    // 执行
                    execute(PlayerTopTypeEnum.VAULT);
                } finally {
                    VAULT_LOCK.release();
                }
            }
        }.runTaskTimerAsynchronously(PlayerTop.getInstance(), 20 * 60, ConfigUtil.CONFIG.getLong("task.vault") * 20);
    }

    /**
     * 玩家点券
     */
    private static void playerPointsTask() {
        // 未加载到插件,定时任务不启动
        if (PlayerTop.getPlayerPoints() == null) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!PLAYER_POINTS_LOCK.tryAcquire()) {
                    return;
                }
                try {
                    // 执行
                    execute(PlayerTopTypeEnum.PLAYER_POINTS);
                } finally {
                    PLAYER_POINTS_LOCK.release();
                }
            }
        }.runTaskTimerAsynchronously(PlayerTop.getInstance(), 20 * 60, ConfigUtil.CONFIG.getLong("task.playerPoints") * 20);
    }

    /**
     * 玩家称号数量
     */
    private static void playerTitleNumberTask() {
        // 未加载到插件,定时任务不启动
        if (!PlayerTop.USE_TITLE) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!PLAYER_TITLE_NUMBER_LOCK.tryAcquire()) {
                    return;
                }
                try {
                    // 执行
                    execute(PlayerTopTypeEnum.PLAYER_TITLE_NUMBER);
                } finally {
                    PLAYER_TITLE_NUMBER_LOCK.release();
                }
            }
        }.runTaskTimerAsynchronously(PlayerTop.getInstance(), 20 * 60, ConfigUtil.CONFIG.getLong("task.playerTitleNumber") * 20);
    }

    /**
     * 玩家称号币数量
     */
    private static void playerTitleCoinTask() {
        // 未加载到插件,定时任务不启动
        if (!PlayerTop.USE_TITLE) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!PLAYER_TITLE_COIN_LOCK.tryAcquire()) {
                    return;
                }
                try {
                    // 执行
                    execute(PlayerTopTypeEnum.PLAYER_TITLE_COIN);
                } finally {
                    PLAYER_TITLE_COIN_LOCK.release();
                }
            }
        }.runTaskTimerAsynchronously(PlayerTop.getInstance(), 20 * 60, ConfigUtil.CONFIG.getLong("task.playerTitleCoin") * 20);
    }

    /**
     * 玩家任务币数量
     */
    private static void playerTaskCoinTask() {
        // 未加载到插件,定时任务不启动
        if (!PlayerTop.USE_TASK) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!PLAYER_TASK_COIN_LOCK.tryAcquire()) {
                    return;
                }
                try {
                    // 执行
                    execute(PlayerTopTypeEnum.PLAYER_TASK_COIN);
                } finally {
                    PLAYER_TASK_COIN_LOCK.release();
                }
            }
        }.runTaskTimerAsynchronously(PlayerTop.getInstance(), 20 * 60, ConfigUtil.CONFIG.getLong("task.playerTaskCoin") * 20);
    }

    /**
     * 玩家公会贡献数量
     */
    private static void playerGuildMoneyTask() {
        // 未加载到插件,定时任务不启动
        if (!PlayerTop.USE_GUILD) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!PLAYER_GUILD_MONEY_LOCK.tryAcquire()) {
                    return;
                }
                try {
                    // 执行
                    execute(PlayerTopTypeEnum.PLAYER_GUILD_MONEY);
                } finally {
                    PLAYER_GUILD_MONEY_LOCK.release();
                }
            }
        }.runTaskTimerAsynchronously(PlayerTop.getInstance(), 20 * 60, ConfigUtil.CONFIG.getLong("task.playerGuildMoney") * 20);
    }

    /**
     * 同步方法
     *
     * @param topTypeEnum 类型
     */
    private static void execute(PlayerTopTypeEnum topTypeEnum) {
        long start = System.currentTimeMillis();
        // 全部玩家
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        int playerNum = 0;
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            String playerName = offlinePlayer.getName();
            if (StrUtil.isEmpty(playerName)) {
                continue;
            }
            int number = 0;
            double vault = 0.0;
            switch (topTypeEnum) {
                case VAULT:
                    vault = VaultUtil.getPlayerVault(playerName);
                    number = (int) vault;
                    break;
                case PLAYER_POINTS:
                    number = PlayerPointsUtil.getPlayerPoints(offlinePlayer.getUniqueId());
                    vault = number;
                    break;
                case PLAYER_TITLE_COIN:
                    number = PlayerTitleUtil.getPlayerTitleCoin(playerName);
                    vault = number;
                    break;
                case PLAYER_TITLE_NUMBER:
                    number = PlayerTitleUtil.getPlayerTitleNumber(playerName);
                    vault = number;
                    break;
                case PLAYER_TASK_COIN:
                    number = PlayerTaskUtil.getPlayerTaskNumber(playerName);
                    vault = number;
                    break;
                case PLAYER_GUILD_MONEY:
                    number = PlayerGuildUtil.getPlayerGuildMoney(playerName);
                    vault = number;
                    break;
                default:
                    break;
            }
            TopPlayerService.getInstance().updateByPlayer(topTypeEnum, playerName, offlinePlayer.getUniqueId(), number, vault);
            playerNum++;
        }
        // 刷新排行榜
        Bukkit.getScheduler().runTask(PlayerGuild.getInstance(), () -> TopTask.refreshHd(topTypeEnum));
        MessageApi.sendConsoleDebugMessage("同步" + playerNum + "位玩家" + topTypeEnum.getName() + ",消耗ms:" + (System.currentTimeMillis() - start));
    }

}