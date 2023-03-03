package cn.handyplus.top.util;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.enter.TopPlayer;
import cn.handyplus.top.hook.HdUtil;
import cn.handyplus.top.hook.JobUtil;
import cn.handyplus.top.hook.McMmoUtil;
import cn.handyplus.top.hook.PlaceholderApiUtil;
import cn.handyplus.top.hook.PlayerGuildUtil;
import cn.handyplus.top.hook.PlayerPointsUtil;
import cn.handyplus.top.hook.PlayerTaskUtil;
import cn.handyplus.top.hook.PlayerTitleUtil;
import cn.handyplus.top.hook.VaultUtil;
import cn.handyplus.top.param.PlayerPapiHd;
import cn.handyplus.top.service.TopPapiPlayerService;
import cn.handyplus.top.service.TopPlayerService;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
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
import java.util.stream.Collectors;

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
        }.runTaskTimerAsynchronously(PlayerTop.getInstance(), 20 * 30, ConfigUtil.CONFIG.getLong("task", 300) * 20);
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
            MessageApi.sendMessage(sender, "一. 开始获取排行数据,请耐心等待...");
        }
        // 全部玩家
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        boolean isOp = ConfigUtil.CONFIG.getBoolean("isOp");
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            String playerName = offlinePlayer.getName();
            if (StrUtil.isEmpty(playerName)) {
                continue;
            }
            // 构建内部数据
            TopPlayer topPlayer = new TopPlayer();
            topPlayer.setPlayerName(playerName);
            topPlayer.setPlayerUuid(offlinePlayer.getUniqueId().toString());
            topPlayer.setOp(false);
            if (isOp) {
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
                topPlayer.setPlayerGuildDonatedMoney(PlayerGuildUtil.getInstance().getPlayerDonatedGuildMoney(playerName));
            }
            // McMmo
            if (PlayerTop.USE_MC_MMO) {
                try {
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
                } catch (Throwable ignored) {
                }
            }
            // jobs
            if (PlayerTop.USE_JOB) {
                Map<String, Integer> levelMap = JobUtil.getInstance().getLevelMap(playerName);
                topPlayer.setJobBrewer(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_BREWER.getOriginalType(), 0));
                topPlayer.setJobBuilder(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_BUILDER.getOriginalType(), 0));
                topPlayer.setJobCrafter(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_CRAFTER.getOriginalType(), 0));
                topPlayer.setJobDigger(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_DIGGER.getOriginalType(), 0));
                topPlayer.setJobEnchanter(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_ENCHANTER.getOriginalType(), 0));
                topPlayer.setJobExplorer(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_EXPLORER.getOriginalType(), 0));
                topPlayer.setJobFarmer(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_FARMER.getOriginalType(), 0));
                topPlayer.setJobFisherman(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_FISHERMAN.getOriginalType(), 0));
                topPlayer.setJobHunter(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_HUNTER.getOriginalType(), 0));
                topPlayer.setJobMiner(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_MINER.getOriginalType(), 0));
                topPlayer.setJobWeaponSmith(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_WEAPON_SMITH.getOriginalType(), 0));
                topPlayer.setJobWoodcutter(levelMap.getOrDefault(PlayerTopTypeEnum.JOBS_WOODCUTTER.getOriginalType(), 0));
            }
            TopPlayerService.getInstance().saveOrUpdate(topPlayer);
            // papi数据处理
            for (String papiType : getPapiList()) {
                TopPapiPlayer topPapiPlayer = new TopPapiPlayer();
                topPapiPlayer.setPlayerName(offlinePlayer.getName());
                topPapiPlayer.setPlayerUuid(offlinePlayer.getUniqueId().toString());
                topPapiPlayer.setOp(false);
                if (isOp) {
                    topPapiPlayer.setOp(offlinePlayer.isOp());
                }
                topPapiPlayer.setPapi(papiType);
                // 判断值是否存在
                String papiValue = PlaceholderApiUtil.set(offlinePlayer, papiType);
                if (StrUtil.isEmpty(papiValue)) {
                    continue;
                }
                // 转化为数字
                Integer number = BaseUtil.isNumericToInt(papiValue);
                if (number == null) {
                    continue;
                }
                topPapiPlayer.setVault(number);
                TopPapiPlayerService.getInstance().saveOrUpdate(topPapiPlayer);
            }
        }
        if (sender != null) {
            MessageApi.sendMessage(sender, "二. 同步" + offlinePlayers.length + "位玩家" + ",已消耗ms:" + (System.currentTimeMillis() - start));
        }
        // 获取数据
        List<PlayerPapiHd> playerPapiHdList = createHd();
        playerPapiHdList.addAll(createPapiHd());
        if (sender != null) {
            MessageApi.sendMessage(sender, "三. 获取构建全息图的数据,已消耗ms:" + (System.currentTimeMillis() - start));
        }
        // 同步处理
        new BukkitRunnable() {
            @Override
            public void run() {
                // 删除现有全息图
                HdUtil.deleteAll();
                if (sender != null) {
                    MessageApi.sendMessage(sender, "四. 删除现有全息图,已消耗ms:" + (System.currentTimeMillis() - start));
                }
                // 生成全息排行榜
                for (PlayerPapiHd playerPapiHd : playerPapiHdList) {
                    HdUtil.create(playerPapiHd.getTextLineList(), playerPapiHd.getLocation(), playerPapiHd.getMaterial());
                }
                if (sender != null) {
                    MessageApi.sendMessage(sender, "五. 全部流程完成,本次刷新" + playerPapiHdList.size() + "全息图排行,已消耗ms:" + (System.currentTimeMillis() - start));
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
                for (int i = 0; i < topPapiPlayerList.size(); i++) {
                    textLineList.add(TopUtil.getPapiContent(lore, topPapiPlayerList.get(i), i + 1));
                }
            }
            PlayerPapiHd playerPapiHd = PlayerPapiHd.builder().textLineList(textLineList).location(location).material(material).build();
            playerPapiHdList.add(playerPapiHd);
        }
        return playerPapiHdList;
    }

    /**
     * 获取配置的变量
     *
     * @return 变量合集
     */
    private static List<String> getPapiList() {
        // 一级目录
        Map<String, Object> values = ConfigUtil.PAPI_CONFIG.getValues(false);
        if (values.isEmpty()) {
            return new ArrayList<>();
        }
        // 获取变量类型
        List<String> papiTypeList = getPapiTypeList(values);
        if (CollUtil.isEmpty(papiTypeList)) {
            return new ArrayList<>();
        }
        return papiTypeList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取启用的papi类型
     *
     * @param values 类型
     * @return papi类型
     */
    private static List<String> getPapiTypeList(Map<String, Object> values) {
        List<String> papiTypeList = new ArrayList<>();
        for (String type : values.keySet()) {
            // 二级目录
            MemorySection memorySection = (MemorySection) values.get(type);
            if (memorySection == null) {
                continue;
            }
            // 判断是否开启状态
            boolean enable = memorySection.getBoolean("enable");
            if (!enable) {
                continue;
            }
            String papi = memorySection.getString("papi", "");
            if (StrUtil.isNotEmpty(papi)) {
                papiTypeList.add(papi);
            }
        }
        return papiTypeList;
    }

}