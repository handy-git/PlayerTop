package cn.handyplus.top.service;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.db.Compare;
import cn.handyplus.lib.db.Db;
import cn.handyplus.lib.db.Tx;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.top.core.AsyncTask;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.util.ConfigUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 玩家papi排行数据
 *
 * @author handy
 * @since 1.1.8
 */
public class TopPapiPlayerService {
    private TopPapiPlayerService() {
    }

    private static class SingletonHolder {
        private static final TopPapiPlayerService INSTANCE = new TopPapiPlayerService();
    }

    public static TopPapiPlayerService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 批量新增或更新数据
     *
     * @param sender            控制台
     * @param topPapiPlayerList 批量记录
     * @since 1.2.2
     */
    public void replace(CommandSender sender, List<TopPapiPlayer> topPapiPlayerList) {
        if (CollUtil.isEmpty(topPapiPlayerList)) {
            return;
        }
        // 判断是否过滤op
        List<String> opUidList = new ArrayList<>();
        boolean isOp = ConfigUtil.CONFIG.getBoolean("isOp");
        if (isOp) {
            opUidList.addAll(AsyncTask.getOpUidList());
        }
        // 过滤掉黑名单的
        List<String> blacklist = ConfigUtil.CONFIG.getStringList("blacklist");
        // 过滤掉值
        List<Long> filterList = ConfigUtil.CONFIG.getLongList("filter");
        // 分组排序
        List<TopPapiPlayer> saveTopPapiPlayerList = new ArrayList<>();
        Map<String, List<TopPapiPlayer>> topPapiPlayerGroupList = topPapiPlayerList.stream().collect(Collectors.groupingBy(TopPapiPlayer::getPapi));
        for (String papi : topPapiPlayerGroupList.keySet()) {
            List<TopPapiPlayer> papiList = topPapiPlayerGroupList.get(papi);
            // 进行过滤配置的值
            papiList = CollUtil.isNotEmpty(filterList) ? papiList.stream().filter(s -> !filterList.contains(s.getVault().longValue())).collect(Collectors.toList()) : papiList;
            // 保存离线数据
            List<String> playerUuidList = papiList.stream().map(TopPapiPlayer::getPlayerUuid).distinct().collect(Collectors.toList());
            playerUuidList.addAll(opUidList);
            List<TopPapiPlayer> offTopPapiPlayerList = this.findByPlayerUuids(playerUuidList, blacklist, papi, filterList);
            offTopPapiPlayerList.forEach(player -> player.setUpdateTime(new Date()));
            papiList.addAll(offTopPapiPlayerList);
            if (CollUtil.isEmpty(papiList)) {
                continue;
            }
            // 判断排序
            if ("desc".equalsIgnoreCase(papiList.get(0).getSort())) {
                papiList = papiList.stream().peek(player -> player.setSort("desc")).collect(Collectors.toList());
                papiList = papiList.stream().sorted(Comparator.comparing(TopPapiPlayer::getVault).reversed()).collect(Collectors.toList());
            } else {
                papiList = papiList.stream().peek(player -> player.setSort("asc")).collect(Collectors.toList());
                papiList = papiList.stream().sorted(Comparator.comparing(TopPapiPlayer::getVault)).collect(Collectors.toList());
            }
            for (int i = 0; i < papiList.size(); i++) {
                papiList.get(i).setRank(i + 1);
            }
            saveTopPapiPlayerList.addAll(papiList);
        }
        // 只操作本次操作的变量
        List<String> papiList = saveTopPapiPlayerList.stream().map(TopPapiPlayer::getPapi).distinct().collect(Collectors.toList());
        if (sender != null) {
            int count = this.countByPapi(papiList);
            MessageUtil.sendMessage(sender, "当前数据库条数:" + count + "本次同步条数:" + saveTopPapiPlayerList.size());
        }
        // 使用事物进行处理
        Tx.use().tx(tx -> {
            // 删除全部
            this.deleteByPapi(papiList);
            // 批量添加
            this.addBatch(saveTopPapiPlayerList);
        });
    }

    /**
     * 根据名称分页查询
     *
     * @param papi     类型
     * @param pageNum  页数
     * @param pageSize 条数
     * @return TopPapiPlayer
     */
    public List<TopPapiPlayer> page(String papi, Integer pageNum, Integer pageSize) {
        Db<TopPapiPlayer> db = Db.use(TopPapiPlayer.class);
        Compare<TopPapiPlayer> where = db.where();
        where.limit(pageNum, pageSize).eq(TopPapiPlayer::getPapi, papi);
        where.orderByAsc(TopPapiPlayer::getRank);
        return db.execution().page().getRecords();
    }

    /**
     * 根据uid和类型查询
     *
     * @param uuid uid
     * @param type 类型
     * @return TopPapiPlayer
     * @since 1.2.2
     */
    public Optional<TopPapiPlayer> findByUidAndType(String uuid, String type) {
        Db<TopPapiPlayer> db = Db.use(TopPapiPlayer.class);
        db.where().eq(TopPapiPlayer::getPlayerUuid, uuid)
                .eq(TopPapiPlayer::getPapi, type);
        return db.execution().selectOne();
    }

    /**
     * 根据排行和类型查询
     *
     * @param rank rank
     * @param type 类型
     * @return TopPapiPlayer
     * @since 1.2.2
     */
    public Optional<TopPapiPlayer> findByRankAndType(Integer rank, String type) {
        Db<TopPapiPlayer> db = Db.use(TopPapiPlayer.class);
        db.where().eq(TopPapiPlayer::getRank, rank)
                .eq(TopPapiPlayer::getPapi, type);
        return db.execution().selectOne();
    }

    /**
     * 批量新增
     *
     * @param topPapiPlayerList 入参
     * @since 1.2.2
     */
    private void addBatch(List<TopPapiPlayer> topPapiPlayerList) {
        Db.use(TopPapiPlayer.class).execution().insertBatch(topPapiPlayerList);
    }

    /**
     * 删除
     *
     * @since 1.2.5
     */
    private void delete() {
        Db.use(TopPapiPlayer.class).execution().delete();
    }

    /**
     * 总条数
     *
     * @since 1.3.7
     */
    private int count() {
        return Db.use(TopPapiPlayer.class).execution().count();
    }

    /**
     * 总条数
     *
     * @since 1.5.0
     */
    private int countByPapi(List<String> papiList) {
        Db<TopPapiPlayer> use = Db.use(TopPapiPlayer.class);
        use.where().in(TopPapiPlayer::getPapi, papiList);
        return use.execution().count();
    }

    /**
     * 删除
     *
     * @param papi 变量
     * @return 数量
     * @since 1.3.4
     */
    public int deleteByPapi(String papi) {
        Db<TopPapiPlayer> use = Db.use(TopPapiPlayer.class);
        use.where().eq(TopPapiPlayer::getPapi, papi);
        return use.execution().delete();
    }

    /**
     * 删除
     *
     * @param papiList 变量
     * @since 1.5.0
     */
    public void deleteByPapi(List<String> papiList) {
        Db<TopPapiPlayer> use = Db.use(TopPapiPlayer.class);
        use.where().in(TopPapiPlayer::getPapi, papiList);
        use.execution().delete();
    }

    /**
     * 根据uid not in查询
     *
     * @param playerUuidList 用户uid
     * @param blackNameList  黑名单name
     * @param papi           变量
     * @param filterList     需要过滤的值
     * @return TopPapiPlayer
     * @since 1.2.5
     */
    public List<TopPapiPlayer> findByPlayerUuids(List<String> playerUuidList, List<String> blackNameList, String papi, List<Long> filterList) {
        Db<TopPapiPlayer> db = Db.use(TopPapiPlayer.class);
        db.where().notIn(CollUtil.isNotEmpty(playerUuidList), TopPapiPlayer::getPlayerUuid, playerUuidList)
                .notIn(CollUtil.isNotEmpty(blackNameList), TopPapiPlayer::getPlayerName, blackNameList)
                .notIn(CollUtil.isNotEmpty(filterList), TopPapiPlayer::getVault, filterList)
                .eq(TopPapiPlayer::getPapi, papi);
        return db.execution().list();
    }

}