package cn.handyplus.top.service;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.db.Compare;
import cn.handyplus.lib.db.Db;
import cn.handyplus.lib.db.Tx;
import cn.handyplus.top.enter.TopPapiPlayer;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
     * @param playerUuid uid
     * @param papi       类型
     * @return TopPapiPlayer
     * @since 1.2.2
     */
    public Optional<TopPapiPlayer> findByUidAndType(String playerUuid, String papi) {
        Db<TopPapiPlayer> db = Db.use(TopPapiPlayer.class);
        db.where().eq(TopPapiPlayer::getPlayerUuid, playerUuid).eq(TopPapiPlayer::getPapi, papi);
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
     * @param topPapiPlayer 入参
     * @since 1.5.1
     */
    private void add(TopPapiPlayer topPapiPlayer) {
        Db.use(TopPapiPlayer.class).execution().insert(topPapiPlayer);
    }

    /**
     * 更新值
     *
     * @param topPapiPlayerList 数据
     * @since 1.5.1
     */
    public void setVault(List<TopPapiPlayer> topPapiPlayerList) {
        if (CollUtil.isEmpty(topPapiPlayerList)) {
            return;
        }
        // 改为循环操作，批量先删除后新增多bc子服容易出问题
        Tx.use().tx(tx -> topPapiPlayerList.forEach(this::setVault));
    }

    /**
     * 更新值
     *
     * @param topPapiPlayer 数据
     * @since 1.5.1
     */
    private void setVault(TopPapiPlayer topPapiPlayer) {
        // 新增
        if (topPapiPlayer.getId() == null) {
            topPapiPlayer.setCreateTime(new Date());
            this.add(topPapiPlayer);
            return;
        }
        // 更新
        Db<TopPapiPlayer> use = Db.use(TopPapiPlayer.class);
        use.update().set(TopPapiPlayer::getVault, topPapiPlayer.getVault())
                .set(TopPapiPlayer::getRank, topPapiPlayer.getRank())
                .set(TopPapiPlayer::getPlayerName, topPapiPlayer.getPlayerName())
                .set(TopPapiPlayer::getUpdateTime, new Date());
        use.execution().updateById(topPapiPlayer.getId());
    }

    /**
     * 根类型查询
     *
     * @param papi 类型
     * @return TopPapiPlayer
     * @since 1.5.1
     */
    public List<TopPapiPlayer> findByPapi(String papi) {
        Db<TopPapiPlayer> db = Db.use(TopPapiPlayer.class);
        db.where().eq(TopPapiPlayer::getPapi, papi);
        return db.execution().list();
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

}