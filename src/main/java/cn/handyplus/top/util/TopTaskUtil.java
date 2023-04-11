package cn.handyplus.top.util;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.core.AsyncTask;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.hook.HdUtil;
import cn.handyplus.top.param.PlayerPapiHd;
import cn.handyplus.top.service.TopPapiPlayerService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
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
     * 30s后初始化定时任务
     */
    public static void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                setToDataToLock(null);
            }
        }.runTaskTimerAsynchronously(PlayerTop.getInstance(), 20 * 60, ConfigUtil.CONFIG.getLong("task", 300) * 20);
    }

    /**
     * 加锁可外部调用
     */
    public static void setToDataToLock(CommandSender sender) {
        if (!TASK_LOCK.tryAcquire()) {
            return;
        }
        try {
            // 执行
            setTopData(sender);
        } finally {
            TASK_LOCK.release();
        }
    }

    /**
     * 设置变量数据
     */
    private static void setTopData(CommandSender sender) {
        long start = System.currentTimeMillis();
        if (sender != null) {
            MessageApi.sendMessage(sender, "一. 开始获取排行数据,请耐心等待,当前进度: 1/6");
        }
        // 获取要刷新的玩家信息
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        List<TopPapiPlayer> topPapiPlayerList = AsyncTask.supplyAsync(offlinePlayers);
        if (sender != null) {
            MessageApi.sendMessage(sender, "二. 同步" + offlinePlayers.length + "位玩家变量" + ",已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 2/6");
        }
        // 替换数据
        TopPapiPlayerService.getInstance().replace(topPapiPlayerList);
        if (sender != null) {
            MessageApi.sendMessage(sender, "三. 保存" + offlinePlayers.length + "位玩家数据" + ",已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 3/6");
        }
        // 获取数据
        List<PlayerPapiHd> playerPapiHdList = createHd();
        playerPapiHdList.addAll(createPapiHd());
        if (sender != null) {
            MessageApi.sendMessage(sender, "四. 获取构建全息图的数据,已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 4/6");
        }
        // 同步处理
        new BukkitRunnable() {
            @Override
            public void run() {
                // 删除现有全息图
                HdUtil.deleteAll();
                if (sender != null) {
                    MessageApi.sendMessage(sender, "五. 删除现有全息图,已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 5/6");
                }
                // 生成全息排行榜
                if (CollUtil.isEmpty(playerPapiHdList)) {
                    return;
                }
                for (PlayerPapiHd playerPapiHd : playerPapiHdList) {
                    HdUtil.create(playerPapiHd.getTextLineList(), playerPapiHd.getLocation(), playerPapiHd.getMaterial());
                }
                if (sender != null) {
                    MessageApi.sendMessage(sender, "六. 全部流程完成,本次刷新" + playerPapiHdList.size() + "全息图排行,已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 6/6");
                }
            }
        }.runTask(PlayerTop.getInstance());
    }

    /**
     * 初始化全息图
     */
    private static List<PlayerPapiHd> createHd() {
        // 一级目录
        Map<String, Object> values = ConfigUtil.HD_CONFIG.getValues(false);
        List<PlayerPapiHd> playerPapiHdList = new ArrayList<>();
        for (String type : values.keySet()) {
            // 二级目录
            MemorySection memorySection = (MemorySection) values.get(type);
            if (memorySection == null) {
                continue;
            }
            String world = memorySection.getString("world", "");
            double x = memorySection.getDouble("x");
            double y = memorySection.getDouble("y");
            double z = memorySection.getDouble("z");
            // 获取类型
            PlayerTopTypeEnum playerTopTypeEnum = PlayerTopTypeEnum.getType(type);
            if (playerTopTypeEnum == null) {
                continue;
            }
            // 判断是否开启状态
            boolean enable = memorySection.getBoolean("enable");
            if (!enable) {
                continue;
            }
            Location location = new Location(Bukkit.getWorld(world), x, y, z);
            // 新增全息图
            PlayerPapiHd playerPapiHd = TopUtil.createHd(playerTopTypeEnum, location);
            playerPapiHdList.add(playerPapiHd);
        }
        return playerPapiHdList;
    }

    /**
     * 同步处理papi全息图
     */
    private static List<PlayerPapiHd> createPapiHd() {
        // 一级目录
        Map<String, Object> values = ConfigUtil.PAPI_CONFIG.getValues(false);
        if (values.isEmpty()) {
            return new ArrayList<>();
        }
        // 设置对应属性
        List<PlayerPapiHd> playerPapiHdList = new ArrayList<>();
        for (String type : values.keySet()) {
            // 二级目录
            MemorySection memorySection = (MemorySection) values.get(type);
            if (memorySection == null) {
                continue;
            }
            // 判断是否开启
            boolean enable = memorySection.getBoolean("enable");
            if (!enable) {
                continue;
            }
            String papi = memorySection.getString("papi", "");
            int line = memorySection.getInt("line", 10);
            String material = memorySection.getString("material", "");
            String title = memorySection.getString("title", "");
            String lore = memorySection.getString("lore", "");
            // 获取位置
            String world = memorySection.getString("world", "");
            if (StrUtil.isEmpty(world)) {
                MessageApi.sendConsoleMessage("papi.yml里节点:" + type + ",配置异常");
                continue;
            }
            double x = memorySection.getDouble("x");
            double y = memorySection.getDouble("y");
            double z = memorySection.getDouble("z");
            Location location = new Location(Bukkit.getWorld(world), x, y, z);
            List<String> textLineList = new ArrayList<>();
            if (StrUtil.isNotEmpty(title)) {
                textLineList.add(title);
            }
            // 对应节点数据
            List<TopPapiPlayer> topPapiPlayerList = TopPapiPlayerService.getInstance().page(papi, 1, line);
            if (CollUtil.isNotEmpty(topPapiPlayerList)) {
                // 判断有数据 进行构建行
                for (TopPapiPlayer topPapiPlayer : topPapiPlayerList) {
                    textLineList.add(TopUtil.getContent(lore, topPapiPlayer));
                }
            }
            PlayerPapiHd playerPapiHd = PlayerPapiHd.builder().textLineList(textLineList).location(location).material(material).build();
            playerPapiHdList.add(playerPapiHd);
        }
        return playerPapiHdList;
    }

}