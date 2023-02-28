package cn.handyplus.top.service;

import cn.handyplus.lib.db.Compare;
import cn.handyplus.lib.db.Db;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.util.ConfigUtil;

import java.util.List;

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
     * 新增或更新数据
     *
     * @param topPapiPlayer 记录
     */
    public synchronized void saveOrUpdate(TopPapiPlayer topPapiPlayer) {
        TopPapiPlayer top = this.findByPlayerNameAndPapi(topPapiPlayer.getPlayerName(), topPapiPlayer.getPapi());
        if (top == null) {
            this.add(topPapiPlayer);
            return;
        }
        this.update(topPapiPlayer);
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
        // 是否包含op
        if (ConfigUtil.CONFIG.getBoolean("isOp")) {
            where.eq(TopPapiPlayer::getOp, false);
        }
        where.orderByDesc(TopPapiPlayer::getVault);
        return db.execution().page().getRecords();
    }

    /**
     * 新增
     *
     * @param topPapiPlayer 入参
     */
    private void add(TopPapiPlayer topPapiPlayer) {
        Db.use(TopPapiPlayer.class).execution().insert(topPapiPlayer);
    }

    /**
     * 根据玩家名查询
     *
     * @param playerName 玩家名
     * @param papi       变量
     * @return 数据
     */
    private TopPapiPlayer findByPlayerNameAndPapi(String playerName, String papi) {
        Db<TopPapiPlayer> use = Db.use(TopPapiPlayer.class);
        use.where().eq(TopPapiPlayer::getPlayerName, playerName)
                .eq(TopPapiPlayer::getPapi, papi);
        return use.execution().selectOne();
    }

    /**
     * 根据玩家名更新
     *
     * @param topPapiPlayer 入参
     */
    private void update(TopPapiPlayer topPapiPlayer) {
        Db<TopPapiPlayer> use = Db.use(TopPapiPlayer.class);
        use.update().set(TopPapiPlayer::getOp, topPapiPlayer.getOp())
                .set(TopPapiPlayer::getVault, topPapiPlayer.getVault());
        use.where().eq(TopPapiPlayer::getPlayerName, topPapiPlayer.getPlayerName())
                .eq(TopPapiPlayer::getPapi, topPapiPlayer.getPapi());
        use.execution().update();
    }

}