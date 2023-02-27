package cn.handyplus.top.util;

import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
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
import cn.handyplus.top.param.PlayerPapi;
import cn.handyplus.top.param.PlayerPapiHd;
import cn.handyplus.top.service.TopPlayerService;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.MemorySection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
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
     * 初始化定时任务
     */
    public static void init() {
        // 初始化全息图
        createHd();
        createPapiHd();
        // 定时任务获取数据
        if (ConfigUtil.CONFIG.getLong("task", 300) > 0) {
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
        }
        MessageApi.sendConsoleDebugMessage("同步" + offlinePlayers.length + "位玩家" + ",消耗ms:" + (System.currentTimeMillis() - start));
        // 刷新papi排行榜
        TopTaskUtil.createPapiHd();
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
            String world = memorySection.getString("world", "");
            double x = memorySection.getDouble("x");
            double y = memorySection.getDouble("y");
            double z = memorySection.getDouble("z");
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

    /**
     * 创建papi排行
     */
    public static void createPapiHd() {
        // 一级目录
        Map<String, Object> values = ConfigUtil.PAPI_CONFIG.getValues(false);
        if (values.isEmpty()) {
            return;
        }
        // 获取变量类型
        List<String> papiTypeList = getPapiTypeList(values);
        if (CollUtil.isEmpty(papiTypeList)) {
            return;
        }
        // 获取全部玩家的变量值
        Map<String, List<PlayerPapi>> playerPapiListMap = getPlayerPapiListMap(papiTypeList);
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
            List<PlayerPapi> playerPapiChildList = playerPapiListMap.get(papi);
            if (CollUtil.isNotEmpty(playerPapiChildList)) {
                // 排序并取出数据
                List<PlayerPapi> playerPapiTopList = playerPapiChildList.stream().sorted(Comparator.comparing(PlayerPapi::getPapiValue).reversed()).limit(line).collect(Collectors.toList());
                // 判断有数据 进行构建行
                for (int i = 0; i < playerPapiTopList.size(); i++) {
                    textLineList.add(TopUtil.getPapiContent(lore, playerPapiTopList.get(i), i + 1));
                }
            }
            PlayerPapiHd playerPapiHd = PlayerPapiHd.builder().textLineList(textLineList).location(location).material(material).build();
            playerPapiHdList.add(playerPapiHd);
        }
        // 处理papi全息图
        createPapiHd(playerPapiHdList);
    }

    /**
     * 同步处理papi全息图
     *
     * @param playerPapiHdList papi全息图信息
     */
    private static void createPapiHd(List<PlayerPapiHd> playerPapiHdList) {
        if (CollUtil.isEmpty(playerPapiHdList)) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                // 刷新排行榜
                for (PlayerPapiHd playerPapiHd : playerPapiHdList) {
                    HdUtil.delete(playerPapiHd.getLocation());
                    HdUtil.create(playerPapiHd.getTextLineList(), playerPapiHd.getLocation(), playerPapiHd.getMaterial());
                }
            }
        }.runTask(PlayerTop.getInstance());
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

    /**
     * 获取玩家的变量数据
     *
     * @param papiTypeList 变量类型
     * @return 变量数据
     */
    private static Map<String, List<PlayerPapi>> getPlayerPapiListMap(List<String> papiTypeList) {
        List<PlayerPapi> playerPapiList = new ArrayList<>();
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        boolean isOp = ConfigUtil.CONFIG.getBoolean("isOp");

        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            String playerName = offlinePlayer.getName();
            if (StrUtil.isEmpty(playerName)) {
                continue;
            }
            if (isOp && offlinePlayer.isOp()) {
                continue;
            }
            for (String papiType : papiTypeList) {
                String papiValue = PlaceholderApiUtil.set(offlinePlayer, papiType).trim();
                PlayerPapi playerPapi = PlayerPapi.builder().playerName(playerName).playerUuid(offlinePlayer.getUniqueId()).papiType(papiType).papiValue(papiValue).build();
                playerPapiList.add(playerPapi);
            }
        }
        // 根据类型分组
        return playerPapiList.stream().collect(Collectors.groupingBy(PlayerPapi::getPapiType));
    }

}