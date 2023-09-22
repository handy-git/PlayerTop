package cn.handyplus.top.core;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.hook.JobUtil;
import cn.handyplus.top.hook.McMmoUtil;
import cn.handyplus.top.hook.PlaceholderApiUtil;
import cn.handyplus.top.hook.PlayerGuildUtil;
import cn.handyplus.top.hook.PlayerPointsUtil;
import cn.handyplus.top.hook.PlayerTaskUtil;
import cn.handyplus.top.hook.PlayerTitleUtil;
import cn.handyplus.top.hook.VaultUtil;
import cn.handyplus.top.util.ConfigUtil;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 核心方法
 *
 * @author handy
 */
public class AsyncTask {

    /**
     * 获取在线玩家
     *
     * @return TopPapiPlayer
     */
    public static List<OfflinePlayer> getOnlineList() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    /**
     * 获取离线玩家
     *
     * @return TopPapiPlayer
     */
    public static List<OfflinePlayer> getOfflineList() {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        List<OfflinePlayer> playerList = new ArrayList<>(offlinePlayers.length);
        Collections.addAll(playerList, offlinePlayers);
        return playerList;
    }

    /**
     * 刷新玩家排行数据
     *
     * @param playerList 玩家
     * @return TopPapiPlayer
     */
    public static List<TopPapiPlayer> supplyOfflineAsync(List<OfflinePlayer> playerList) {
        List<TopPapiPlayer> topPapiPlayerList = new ArrayList<>();
        if (CollUtil.isEmpty(playerList)) {
            return topPapiPlayerList;
        }
        // 判断是否过滤op
        boolean isOp = ConfigUtil.CONFIG.getBoolean("isOp");
        if (isOp) {
            playerList = playerList.stream().filter(s -> !s.isOp()).collect(Collectors.toList());
        }
        // 过滤掉空名称的
        playerList = playerList.stream().filter(s -> StrUtil.isNotEmpty(s.getName())).collect(Collectors.toList());
        // 过滤掉黑名单的
        List<String> blacklist = ConfigUtil.CONFIG.getStringList("blacklist");
        if (CollUtil.isNotEmpty(blacklist)) {
            playerList = playerList.stream().filter(s -> !blacklist.contains(s.getName())).collect(Collectors.toList());
        }
        // 异步循环获取值
        List<CompletableFuture<List<TopPapiPlayer>>> completableFutureList = new ArrayList<>();
        for (List<OfflinePlayer> players : CollUtil.splitList(playerList, 1000)) {
            CompletableFuture<List<TopPapiPlayer>> completableFuture = CompletableFuture.supplyAsync(() -> getTopPapiPlayerList(players));
            completableFutureList.add(completableFuture);
        }
        if (CollUtil.isEmpty(completableFutureList)) {
            return topPapiPlayerList;
        }
        // 等待获取全部数据出来
        for (CompletableFuture<List<TopPapiPlayer>> listCompletableFuture : completableFutureList) {
            topPapiPlayerList.addAll(listCompletableFuture.join());
        }
        return topPapiPlayerList;
    }

    /**
     * 获取服务器ops
     *
     * @return op uid
     */
    public static List<String> getOpUidList() {
        List<OfflinePlayer> opList = new ArrayList<>(Bukkit.getOperators());
        return opList.stream().map(s -> s.getUniqueId().toString()).collect(Collectors.toList());
    }

    /**
     * 同步获取数据
     *
     * @param offlinePlayers 玩家
     * @return TopPapiPlayer
     */
    private static List<TopPapiPlayer> getTopPapiPlayerList(List<OfflinePlayer> offlinePlayers) {
        List<TopPapiPlayer> topPapiPlayerList = new ArrayList<>();
        topPapiPlayerList.addAll(supplyAsyncApi(offlinePlayers));
        topPapiPlayerList.addAll(supplyAsyncPapi(offlinePlayers));
        return topPapiPlayerList;
    }

    /**
     * 异步获取 根据api获取的值
     *
     * @param offlinePlayers 玩家
     * @return TopPapiPlayer
     */
    private static List<TopPapiPlayer> supplyAsyncApi(List<OfflinePlayer> offlinePlayers) {
        List<TopPapiPlayer> topPapiPlayerList = new ArrayList<>();
        // 开启的数据
        Map<String, String> enableMap = HandyConfigUtil.getStringMapChild(ConfigUtil.CONFIG, "enable");
        // 异步循环获取值
        List<CompletableFuture<List<TopPapiPlayer>>> completableFutureList = new ArrayList<>();
        for (String type : enableMap.keySet()) {
            if (!"true".equalsIgnoreCase(enableMap.get(type))) {
                continue;
            }
            PlayerTopTypeEnum typeEnum = PlayerTopTypeEnum.getType(type);
            if (typeEnum == null) {
                continue;
            }
            CompletableFuture<List<TopPapiPlayer>> completableFuture = CompletableFuture.supplyAsync(() -> getApiValue(offlinePlayers, typeEnum));
            completableFutureList.add(completableFuture);
        }
        if (CollUtil.isEmpty(completableFutureList)) {
            return topPapiPlayerList;
        }
        // 等待获取全部数据出来
        for (CompletableFuture<List<TopPapiPlayer>> listCompletableFuture : completableFutureList) {
            topPapiPlayerList.addAll(listCompletableFuture.join());
        }
        return topPapiPlayerList;
    }

    /**
     * 异步获取 根据papi获取的值
     *
     * @param offlinePlayers 玩家
     * @return TopPapiPlayer
     */
    private static List<TopPapiPlayer> supplyAsyncPapi(List<OfflinePlayer> offlinePlayers) {
        List<TopPapiPlayer> topPapiPlayerList = new ArrayList<>();
        // papi数据处理
        Map<String, CompletableFuture<List<TopPapiPlayer>>> completableFutureMap = new HashMap<>();
        Map<String, String> papiMap = getPapiMap();
        for (String papiType : papiMap.keySet()) {
            CompletableFuture<List<TopPapiPlayer>> completableFuture = CompletableFuture.supplyAsync(() -> getPapiValue(offlinePlayers, papiType, papiMap.get(papiType)));
            completableFutureMap.put(papiType, completableFuture);
        }
        if (completableFutureMap.isEmpty()) {
            return topPapiPlayerList;
        }
        // 等待获取全部数据出来
        for (String papiType : completableFutureMap.keySet()) {
            try {
                topPapiPlayerList.addAll(completableFutureMap.get(papiType).get(2, TimeUnit.MINUTES));
            } catch (Exception ignored) {
                MessageUtil.sendConsoleMessage("获取" + papiType + "变量数据超时...");
            }
        }
        return topPapiPlayerList;
    }

    /**
     * papi获取的值
     *
     * @param offlinePlayers 玩家集合
     * @param papiType       papi变量
     * @param sort           排序方式
     * @return TopPapiPlayer
     */
    private static List<TopPapiPlayer> getPapiValue(List<OfflinePlayer> offlinePlayers, String papiType, String sort) {
        long start = System.currentTimeMillis();
        MessageUtil.sendConsoleDebugMessage("获取" + papiType + "变量的值开始..");
        List<TopPapiPlayer> topPapiPlayerList = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            // 构建内部数据
            TopPapiPlayer topPapiPlayer = new TopPapiPlayer();
            topPapiPlayer.setPlayerName(offlinePlayer.getName());
            topPapiPlayer.setPlayerUuid(offlinePlayer.getUniqueId().toString());
            topPapiPlayer.setPapi(papiType);
            topPapiPlayer.setSort(sort);
            // 判断值是否存在
            String papiValue = PlaceholderApiUtil.set(offlinePlayer, papiType);
            if (StrUtil.isEmpty(papiValue)) {
                continue;
            }
            // 转化为数字
            Long number = NumberUtil.isNumericToLong(papiValue);
            if (number == null) {
                continue;
            }
            topPapiPlayer.setVault(number);
            topPapiPlayerList.add(topPapiPlayer);
        }
        MessageUtil.sendConsoleDebugMessage("获取" + papiType + "变量的值结束,耗时ms:" + (System.currentTimeMillis() - start));
        return topPapiPlayerList;
    }

    /**
     * api获取的值
     *
     * @param offlinePlayers 玩家集合
     * @param typeEnum       类型
     * @return TopPapiPlayer
     */
    private static List<TopPapiPlayer> getApiValue(List<OfflinePlayer> offlinePlayers, PlayerTopTypeEnum typeEnum) {
        long start = System.currentTimeMillis();
        MessageUtil.sendConsoleDebugMessage("获取" + typeEnum.getType() + "变量的值开始..");
        List<TopPapiPlayer> topPapiPlayerList = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            // 构建内部数据
            TopPapiPlayer topPapiPlayer = new TopPapiPlayer();
            topPapiPlayer.setPlayerName(offlinePlayer.getName());
            topPapiPlayer.setPlayerUuid(offlinePlayer.getUniqueId().toString());
            topPapiPlayer.setPapi(typeEnum.getType());
            topPapiPlayer.setSort("desc");
            topPapiPlayer.setVault(getDataValue(offlinePlayer, typeEnum));
            topPapiPlayerList.add(topPapiPlayer);
        }
        MessageUtil.sendConsoleDebugMessage("获取" + typeEnum.getType() + "变量的值结束,耗时ms:" + (System.currentTimeMillis() - start));
        return topPapiPlayerList;
    }

    /**
     * 获取单独的每个值
     *
     * @param offlinePlayer 玩家
     * @param typeEnum      类型
     * @return 值
     * @since 1.2.2
     */
    private static Long getDataValue(OfflinePlayer offlinePlayer, PlayerTopTypeEnum typeEnum) {
        Long dataValue = 0L;
        // 处理McMmo未加载
        if (typeEnum.getType().contains("mcMmo") && !PlayerTop.USE_MC_MMO) {
            return dataValue;
        }
        // 处理jobs未加载
        if (typeEnum.getType().contains("jobs") && !PlayerTop.USE_JOB) {
            return dataValue;
        }
        Map<String, Long> jobLevelMap;
        switch (typeEnum) {
            case VAULT:
                dataValue = VaultUtil.getInstance().getPlayerVault(offlinePlayer);
                break;
            case PLAYER_POINTS:
                dataValue = PlayerPointsUtil.getInstance().getPlayerPoints(offlinePlayer.getUniqueId());
                break;
            case PLAYER_TITLE_COIN:
                dataValue = PlayerTitleUtil.getInstance().getPlayerTitleCoin(offlinePlayer.getName());
                break;
            case PLAYER_TITLE_NUMBER:
                dataValue = PlayerTitleUtil.getInstance().getPlayerTitleNumber(offlinePlayer.getName());
                break;
            case PLAYER_TASK_COIN:
                dataValue = PlayerTaskUtil.getInstance().getPlayerTaskCoin(offlinePlayer.getName());
                break;
            case PLAYER_GUILD_MONEY:
                dataValue = PlayerGuildUtil.getInstance().getPlayerGuildMoney(offlinePlayer.getName());
                break;
            case PLAYER_GUILD_DONATED_MONEY:
                dataValue = PlayerGuildUtil.getInstance().getPlayerDonatedGuildMoney(offlinePlayer.getName());
                break;
            case PLAYER_GUILD_KILL:
                dataValue = PlayerGuildUtil.getInstance().getPlayerGuildKill(offlinePlayer.getName());
                break;
            case PLAYER_GUILD_DIE:
                dataValue = PlayerGuildUtil.getInstance().getPlayerGuildDie(offlinePlayer.getName());
                break;
            case MC_MMO:
                dataValue = McMmoUtil.getInstance().getPowerLevelOffline(offlinePlayer.getUniqueId());
                break;
            case MC_MMO_AXES:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.AXES.name());
                break;
            case MC_MMO_MINING:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.MINING.name());
                break;
            case MC_MMO_REPAIR:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.REPAIR.name());
                break;
            case MC_MMO_SWORDS:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.SWORDS.name());
                break;
            case MC_MMO_TAMING:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.TAMING.name());
                break;
            case MC_MMO_ALCHEMY:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.ALCHEMY.name());
                break;
            case MC_MMO_ARCHERY:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.ARCHERY.name());
                break;
            case MC_MMO_FISHING:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.FISHING.name());
                break;
            case MC_MMO_SALVAGE:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.SALVAGE.name());
                break;
            case MC_MMO_UNARMED:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.UNARMED.name());
                break;
            case MC_MMO_SMELTING:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.SMELTING.name());
                break;
            case MC_MMO_HERBALISM:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.HERBALISM.name());
                break;
            case MC_MMO_ACROBATICS:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.ACROBATICS.name());
                break;
            case MC_MMO_EXCAVATION:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.EXCAVATION.name());
                break;
            case MC_MMO_WOODCUTTING:
                dataValue = McMmoUtil.getInstance().getLevelOffline(offlinePlayer.getUniqueId(), PrimarySkillType.WOODCUTTING.name());
                break;
            case JOBS_BUILDER:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_BREWER.getOriginalType(), 0L);
                break;
            case JOBS_CRAFTER:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_CRAFTER.getOriginalType(), 0L);
                break;
            case JOBS_DIGGER:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_DIGGER.getOriginalType(), 0L);
                break;
            case JOBS_ENCHANTER:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_ENCHANTER.getOriginalType(), 0L);
                break;
            case JOBS_EXPLORER:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_EXPLORER.getOriginalType(), 0L);
                break;
            case JOBS_FARMER:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_FARMER.getOriginalType(), 0L);
                break;
            case JOBS_FISHERMAN:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_FISHERMAN.getOriginalType(), 0L);
                break;
            case JOBS_HUNTER:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_HUNTER.getOriginalType(), 0L);
                break;
            case JOBS_MINER:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_MINER.getOriginalType(), 0L);
                break;
            case JOBS_WEAPON_SMITH:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_WEAPON_SMITH.getOriginalType(), 0L);
                break;
            case JOBS_WOODCUTTER:
                jobLevelMap = JobUtil.getInstance().getLevelMap(offlinePlayer.getUniqueId());
                dataValue = jobLevelMap.getOrDefault(PlayerTopTypeEnum.JOBS_WOODCUTTER.getOriginalType(), 0L);
                break;
            default:
                break;
        }
        return dataValue;
    }

    /**
     * 获取配置的变量
     *
     * @return 变量合集
     */
    private static Map<String, String> getPapiMap() {
        // 一级目录
        Map<String, Object> values = ConfigUtil.PAPI_CONFIG.getValues(false);
        if (values.isEmpty()) {
            return new HashMap<>();
        }
        // 获取变量类型
        Map<String, String> papiTypeList = getPapiTypeList(values);
        if (papiTypeList.isEmpty()) {
            return new HashMap<>();
        }
        return papiTypeList;
    }

    /**
     * 获取启用的papi类型
     *
     * @param values 类型
     * @return papi类型
     */
    private static Map<String, String> getPapiTypeList(Map<String, Object> values) {
        Map<String, String> papiTypeMap = new HashMap<>();
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
            String sort = memorySection.getString("sort", "desc");
            if (StrUtil.isNotEmpty(papi)) {
                papiTypeMap.put(papi, sort);
            }
        }
        return papiTypeMap;
    }

}
