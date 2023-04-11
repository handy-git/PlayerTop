package cn.handyplus.top.service;

import cn.handyplus.lib.core.CollUtil;
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
     * 批量新增或更新数据
     *
     * @param topPapiPlayerList 批量记录
     * @since 1.2.2
     */
    public void replace(List<TopPapiPlayer> topPapiPlayerList) {
        // 先删除
        this.delete();
        if (CollUtil.isEmpty(topPapiPlayerList)) {
            return;
        }
        // 批量添加
        for (List<TopPapiPlayer> list : CollUtil.splitList(topPapiPlayerList, 500)) {
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
        // 是否包含op
        if (ConfigUtil.CONFIG.getBoolean("isOp")) {
            where.eq(TopPapiPlayer::getOp, false);
        }
        where.orderByDesc(TopPapiPlayer::getVault);
        return db.execution().page().getRecords();
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
     * @since 1.2.2
     */
    private void delete() {
        Db.use(TopPapiPlayer.class).execution().delete();
    }

}