package cn.handyplus.top.util;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.hologram.HdUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.constants.TopConstants;
import cn.handyplus.top.core.AsyncTask;
import cn.handyplus.top.core.PapiRank;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.param.PlayerPapiHd;
import cn.handyplus.top.service.TopPapiPlayerService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;

/**
 * 定时任务
 *
 * @author handy
 */
public class TopTaskUtil {
    private static final Semaphore TASK_LOCK = new Semaphore(1);

    /**
     * 60s后初始化定时任务
     */
    public static void init() {
        long period = ConfigUtil.CONFIG.getLong("task", 300);
        if (period < 1) {
            return;
        }
        // 是否执行初始化
        TopConstants.IS_INIT = ConfigUtil.CONFIG.getBoolean("isInit", false);
        // 同步模式
        boolean syncMode = "online".equalsIgnoreCase(ConfigUtil.CONFIG.getString("syncMode", "online"));
        HandySchedulerUtil.runTaskTimerAsynchronously(() -> setToDataToLock(null, syncMode), 20, period * 20);
    }

    /**
     * 加锁可外部调用
     *
     * @param sender   控制台
     * @param isOnline 是否在线
     */
    public static void setToDataToLock(CommandSender sender, boolean isOnline) {
        if (!TASK_LOCK.tryAcquire()) {
            return;
        }
        try {
            if (TopConstants.IS_INIT && !isOnline) {
                isOnline = true;
                TopConstants.IS_INIT = false;
            }
            // 执行
            setTopData(sender, isOnline);
        } finally {
            TASK_LOCK.release();
        }
    }

    /**
     * 设置变量数据
     */
    private static void setTopData(CommandSender sender, boolean isOnline) {
        long start = System.currentTimeMillis();
        MessageUtil.sendMessage(sender, "1 -> 开始获取排行数据,请耐心等待,当前进度: 1/6");
        // 获取要刷新的玩家信息
        List<OfflinePlayer> offlinePlayers = isOnline ? AsyncTask.getOnlineList() : AsyncTask.getOfflineList();
        // 获取变量数据
        List<TopPapiPlayer> topPapiPlayerList = AsyncTask.supplyOfflineAsync(offlinePlayers);
        MessageUtil.sendMessage(sender, "2 -> 同步" + offlinePlayers.size() + "位玩家变量" + ",已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 2/6");
        // 更新并排序变量数据
        PapiRank.replace(sender, topPapiPlayerList);
        MessageUtil.sendMessage(sender, "3 -> 保存" + offlinePlayers.size() + "位玩家数据" + ",已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 3/6");
        // 获取数据
        List<PlayerPapiHd> playerPapiHdList = createHd();
        playerPapiHdList.addAll(createPapiHd());
        MessageUtil.sendMessage(sender, "4 -> 获取构建全息图的数据,已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 4/6");
        // 同步处理
        HandySchedulerUtil.runTask(() -> {
            // 删除现有全息图
            HdUtil.deleteAll();
            MessageUtil.sendMessage(sender, "5 -> 删除现有全息图,已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 5/6");
            // 生成全息排行榜
            if (CollUtil.isEmpty(playerPapiHdList)) {
                return;
            }
            for (PlayerPapiHd playerPapiHd : playerPapiHdList) {
                HdUtil.create(playerPapiHd.getTextLineList(), playerPapiHd.getLocation(), playerPapiHd.getMaterial(), playerPapiHd.getCustomModelData());
            }
            MessageUtil.sendMessage(sender, "6 -> 全部流程完成,本次刷新" + playerPapiHdList.size() + "全息图排行,已消耗ms:" + (System.currentTimeMillis() - start) + ",当前进度: 6/6");
        });
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
            if (StrUtil.isEmpty(world)) {
                continue;
            }
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
            if (playerPapiHd == null) {
                MessageUtil.sendConsoleMessage(ConfigUtil.LANG_CONFIG.getString("noWorld", "").replace("${world}", world));
                continue;
            }
            playerPapiHdList.add(playerPapiHd);
        }
        return playerPapiHdList;
    }

    /**
     * 同步处理papi全息图
     */
    private static List<PlayerPapiHd> createPapiHd() {
        // 一级目录
        Map<String, Object> values = ConfigUtil.getPapiOneChildMap();
        if (values.isEmpty()) {
            return new ArrayList<>();
        }
        // 设置对应属性
        List<PlayerPapiHd> playerPapiHdList = new ArrayList<>();
        for (String type : values.keySet()) {
            Optional<PlayerPapiHd> papiDataOpt = getPapiData(type, values);
            papiDataOpt.ifPresent(playerPapiHdList::add);
        }
        return playerPapiHdList;
    }

    public static Optional<PlayerPapiHd> getPapiData(String type, Map<String, Object> values) {
        // 二级目录
        MemorySection memorySection = (MemorySection) values.get(type);
        if (memorySection == null) {
            return Optional.empty();
        }
        // 判断是否开启
        boolean enable = memorySection.getBoolean("enable");
        if (!enable) {
            return Optional.empty();
        }
        String papi = memorySection.getString("papi", "");
        int line = memorySection.getInt("line", 10);
        String material = memorySection.getString("material", "");
        int customModelData = memorySection.getInt("custom-model-data", 0);

        String title = memorySection.getString("title", "");
        String lore = memorySection.getString("lore", "");
        // 获取位置
        String world = memorySection.getString("world", "");
        if (StrUtil.isEmpty(world)) {
            return Optional.empty();
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
        return Optional.of(PlayerPapiHd.builder().textLineList(textLineList).location(location).material(material).customModelData(customModelData).build());
    }

}