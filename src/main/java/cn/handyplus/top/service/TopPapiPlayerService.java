package cn.handyplus.top.service;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.db.Compare;
import cn.handyplus.lib.db.Db;
import cn.handyplus.top.core.AsyncTask;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.util.ConfigUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
     * @param topPapiPlayerList 批量记录
     * @since 1.2.2
     */
    public void replace(List<TopPapiPlayer> topPapiPlayerList) {
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
        // 分组排序
        List<TopPapiPlayer> saveTopPapiPlayerList = new ArrayList<>();
        Map<String, List<TopPapiPlayer>> topPapiPlayerGroupList = topPapiPlayerList.stream().collect(Collectors.groupingBy(TopPapiPlayer::getPapi));
        for (String papi : topPapiPlayerGroupList.keySet()) {
            List<TopPapiPlayer> papiList = topPapiPlayerGroupList.get(papi);
            // 保存离线数据
            List<String> playerUuidList = papiList.stream().map(TopPapiPlayer::getPlayerUuid).distinct().collect(Collectors.toList());
            playerUuidList.addAll(opUidList);
            List<TopPapiPlayer> offTopPapiPlayerList = this.findByPlayerUuids(playerUuidList, blacklist, papi);
            papiList.addAll(offTopPapiPlayerList);
            // 排序
            papiList = papiList.stream().sorted(Comparator.comparing(TopPapiPlayer::getVault).reversed()).collect(Collectors.toList());
            for (int i = 0; i < papiList.size(); i++) {
                papiList.get(i).setRank(i + 1);
            }
            saveTopPapiPlayerList.addAll(papiList);
        }
        // 先删除获取到的变量类型
        this.deleteByPapi(new ArrayList<>(topPapiPlayerGroupList.keySet()));
        // ID赋值
        for (int i = 0; i < saveTopPapiPlayerList.size(); i++) {
            saveTopPapiPlayerList.get(i).setId(i + 1);
        }
        // 批量添加
        for (List<TopPapiPlayer> list : CollUtil.splitList(saveTopPapiPlayerList, 1000)) {
            this.addBatch(list);
        }
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
    public TopPapiPlayer findByUidAndType(String uuid, String type) {
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
    public TopPapiPlayer findByRankAndType(Integer rank, String type) {
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
    private void deleteByPapi(List<String> papiList) {
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
     * @return TopPapiPlayer
     * @since 1.2.5
     */
    public List<TopPapiPlayer> findByPlayerUuids(List<String> playerUuidList, List<String> blackNameList, String papi) {
        Db<TopPapiPlayer> db = Db.use(TopPapiPlayer.class);
        db.where().notIn(CollUtil.isNotEmpty(playerUuidList), TopPapiPlayer::getPlayerUuid, playerUuidList)
                .notIn(CollUtil.isNotEmpty(blackNameList), TopPapiPlayer::getPlayerName, blackNameList)
                .eq(TopPapiPlayer::getPapi, papi);
        return db.execution().list();
    }

}