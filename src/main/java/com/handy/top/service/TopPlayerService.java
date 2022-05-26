package com.handy.top.service;

import com.handy.lib.db.Compare;
import com.handy.lib.db.Db;
import com.handy.top.constants.PlayerTopTypeEnum;
import com.handy.top.enter.TopPlayer;

import java.util.List;
import java.util.UUID;

/**
 * 玩家经济
 *
 * @author handy
 */
public class TopPlayerService {
    private TopPlayerService() {
    }

    private static class SingletonHolder {
        private static final TopPlayerService INSTANCE = new TopPlayerService();
    }

    public static TopPlayerService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 根据玩家更新
     *
     * @param topTypeEnum 类型
     * @param playerName  玩家名
     * @param playerUuid  玩家uuid
     * @param number      数量
     * @param vault       小数
     */
    public synchronized void updateByPlayer(PlayerTopTypeEnum topTypeEnum, String playerName, UUID playerUuid, int number, double vault) {
        TopPlayer topPlayer = this.findByPlayerName(playerName);
        if (topPlayer == null) {
            topPlayer = new TopPlayer();
            topPlayer.setPlayerName(playerName);
            topPlayer.setPlayerUuid(playerUuid.toString());
            switch (topTypeEnum) {
                case VAULT:
                    topPlayer.setVault(vault);
                    break;
                case PLAYER_POINTS:
                    topPlayer.setPlayerPoints(number);
                    break;
                case PLAYER_TITLE_COIN:
                    topPlayer.setPlayerTitleCoin(number);
                    break;
                case PLAYER_TITLE_NUMBER:
                    topPlayer.setPlayerTitleNumber(number);
                    break;
                case PLAYER_TASK_COIN:
                    topPlayer.setPlayerTaskCoin(number);
                    break;
                case PLAYER_GUILD_MONEY:
                    topPlayer.setPlayerGuildMoney(number);
                default:
                    break;
            }
            this.add(topPlayer);
            return;
        }
        this.updateByPlayerName(topTypeEnum, playerName, number, vault);
    }

    /**
     * 根据名称分页查询
     *
     * @param topTypeEnum 类型
     * @param pageNum     页数
     * @param pageSize    条数
     * @return TopPlayer
     */
    public List<TopPlayer> page(PlayerTopTypeEnum topTypeEnum, Integer pageNum, Integer pageSize) {
        Db<TopPlayer> db = Db.use(TopPlayer.class);
        Compare<TopPlayer> where = db.where();
        where.limit(pageNum, pageSize);
        switch (topTypeEnum) {
            case VAULT:
                where.orderByDesc(TopPlayer::getVault);
                break;
            case PLAYER_POINTS:
                where.orderByDesc(TopPlayer::getPlayerPoints);
                break;
            case PLAYER_TITLE_COIN:
                where.orderByDesc(TopPlayer::getPlayerTitleCoin);
                break;
            case PLAYER_TITLE_NUMBER:
                where.orderByDesc(TopPlayer::getPlayerTitleNumber);
                break;
            case PLAYER_TASK_COIN:
                where.orderByDesc(TopPlayer::getPlayerTaskCoin);
                break;
            case PLAYER_GUILD_MONEY:
                where.orderByDesc(TopPlayer::getPlayerGuildMoney);
                break;
            default:
                break;
        }
        return db.execution().page().getRecords();
    }

    /**
     * 新增
     *
     * @param topPlayer 入参
     */
    private void add(TopPlayer topPlayer) {
        Db.use(TopPlayer.class).execution().insert(topPlayer);
    }

    /**
     * 根据玩家名查询
     *
     * @param playerName 玩家名
     * @return 数据
     */
    private TopPlayer findByPlayerName(String playerName) {
        Db<TopPlayer> use = Db.use(TopPlayer.class);
        use.where().eq(TopPlayer::getPlayerName, playerName);
        return use.execution().selectOne();
    }

    /**
     * 根据玩家名更新
     *
     * @param topTypeEnum 类型
     * @param playerName  玩家名
     * @param number      数量
     * @param vault       小数量
     */
    private void updateByPlayerName(PlayerTopTypeEnum topTypeEnum, String playerName, int number, double vault) {
        Db<TopPlayer> use = Db.use(TopPlayer.class);
        switch (topTypeEnum) {
            case VAULT:
                use.update().set(TopPlayer::getVault, vault);
                break;
            case PLAYER_POINTS:
                use.update().set(TopPlayer::getPlayerPoints, number);
                break;
            case PLAYER_TITLE_COIN:
                use.update().set(TopPlayer::getPlayerTitleCoin, number);
                break;
            case PLAYER_TITLE_NUMBER:
                use.update().set(TopPlayer::getPlayerTitleNumber, number);
                break;
            case PLAYER_TASK_COIN:
                use.update().set(TopPlayer::getPlayerTaskCoin, number);
                break;
            case PLAYER_GUILD_MONEY:
                use.update().set(TopPlayer::getPlayerGuildMoney, number);
                break;
            default:
                return;
        }
        use.where().eq(TopPlayer::getPlayerName, playerName);
        use.execution().update();
    }

}