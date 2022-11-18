package cn.handyplus.top.service;

import cn.handyplus.lib.db.Compare;
import cn.handyplus.lib.db.Db;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.enter.TopPlayer;
import cn.handyplus.top.util.ConfigUtil;

import java.util.List;

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
     * 新增或更新数据
     *
     * @param topPlayer 记录
     * @since 1.0.3
     */
    public synchronized void saveOrUpdate(TopPlayer topPlayer) {
        TopPlayer top = this.findByPlayerName(topPlayer.getPlayerName());
        if (top == null) {
            this.add(topPlayer);
            return;
        }
        this.update(topPlayer);
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
        // 是否包含op
        if (ConfigUtil.CONFIG.getBoolean("isOp")) {
            where.eq(TopPlayer::getOp, false);
        }
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
            case MC_MMO:
                where.orderByDesc(TopPlayer::getMcMmoSum);
                break;
            case MC_MMO_AXES:
                where.orderByDesc(TopPlayer::getMcMmoAxes);
                break;
            case MC_MMO_MINING:
                where.orderByDesc(TopPlayer::getMcMmoMining);
                break;
            case MC_MMO_REPAIR:
                where.orderByDesc(TopPlayer::getMcMmoRepair);
                break;
            case MC_MMO_SWORDS:
                where.orderByDesc(TopPlayer::getMcMmoSwords);
                break;
            case MC_MMO_TAMING:
                where.orderByDesc(TopPlayer::getMcMmoTaming);
                break;
            case MC_MMO_ALCHEMY:
                where.orderByDesc(TopPlayer::getMcMmoAlchemy);
                break;
            case MC_MMO_ARCHERY:
                where.orderByDesc(TopPlayer::getMcMmoArchery);
                break;
            case MC_MMO_FISHING:
                where.orderByDesc(TopPlayer::getMcMmoFishing);
                break;
            case MC_MMO_SALVAGE:
                where.orderByDesc(TopPlayer::getMcMmoSalvage);
                break;
            case MC_MMO_UNARMED:
                where.orderByDesc(TopPlayer::getMcMmoUnarmed);
                break;
            case MC_MMO_SMELTING:
                where.orderByDesc(TopPlayer::getMcMmoSmelting);
                break;
            case MC_MMO_HERBALISM:
                where.orderByDesc(TopPlayer::getMcMmoHerbalism);
                break;
            case MC_MMO_ACROBATICS:
                where.orderByDesc(TopPlayer::getMcMmoAcrobatics);
                break;
            case MC_MMO_EXCAVATION:
                where.orderByDesc(TopPlayer::getMcMmoExcavation);
                break;
            case MC_MMO_WOODCUTTING:
                where.orderByDesc(TopPlayer::getMcMmoWoodcutting);
                break;
            case PLAYER_GUILD_KILL:
                where.orderByDesc(TopPlayer::getPlayerGuildKill);
                break;
            case PLAYER_GUILD_DIE:
                where.orderByDesc(TopPlayer::getPlayerGuildDie);
                break;
            case JOBS_BREWER:
                where.orderByDesc(TopPlayer::getJobBrewer);
                break;
            case JOBS_BUILDER:
                where.orderByDesc(TopPlayer::getJobBuilder);
                break;
            case JOBS_CRAFTER:
                where.orderByDesc(TopPlayer::getJobCrafter);
                break;
            case JOBS_DIGGER:
                where.orderByDesc(TopPlayer::getJobDigger);
                break;
            case JOBS_ENCHANTER:
                where.orderByDesc(TopPlayer::getJobEnchanter);
                break;
            case JOBS_EXPLORER:
                where.orderByDesc(TopPlayer::getJobExplorer);
                break;
            case JOBS_FARMER:
                where.orderByDesc(TopPlayer::getJobFarmer);
                break;
            case JOBS_FISHERMAN:
                where.orderByDesc(TopPlayer::getJobFisherman);
                break;
            case JOBS_HUNTER:
                where.orderByDesc(TopPlayer::getJobHunter);
                break;
            case JOBS_MINER:
                where.orderByDesc(TopPlayer::getJobMiner);
                break;
            case JOBS_WEAPON_SMITH:
                where.orderByDesc(TopPlayer::getJobWeaponSmith);
                break;
            case JOBS_WOODCUTTER:
                where.orderByDesc(TopPlayer::getJobWoodcutter);
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
     * @param topPlayer 入参
     * @since 1.0.3
     */
    private void update(TopPlayer topPlayer) {
        Db<TopPlayer> use = Db.use(TopPlayer.class);
        use.update()
                .set(TopPlayer::getOp, topPlayer.getOp())
                .set(TopPlayer::getPlayerPoints, topPlayer.getPlayerPoints())
                .set(TopPlayer::getVault, topPlayer.getVault())
                .set(TopPlayer::getPlayerGuildMoney, topPlayer.getPlayerGuildMoney())
                .set(TopPlayer::getPlayerTaskCoin, topPlayer.getPlayerTaskCoin())
                .set(TopPlayer::getPlayerTitleCoin, topPlayer.getPlayerTitleCoin())
                .set(TopPlayer::getPlayerTitleNumber, topPlayer.getPlayerTitleNumber())
                .set(TopPlayer::getMcMmoSum, topPlayer.getMcMmoSum())
                .set(TopPlayer::getMcMmoAcrobatics, topPlayer.getMcMmoAcrobatics())
                .set(TopPlayer::getMcMmoAlchemy, topPlayer.getMcMmoAlchemy())
                .set(TopPlayer::getMcMmoAxes, topPlayer.getMcMmoAxes())
                .set(TopPlayer::getMcMmoExcavation, topPlayer.getMcMmoExcavation())
                .set(TopPlayer::getMcMmoFishing, topPlayer.getMcMmoFishing())
                .set(TopPlayer::getMcMmoHerbalism, topPlayer.getMcMmoHerbalism())
                .set(TopPlayer::getMcMmoMining, topPlayer.getMcMmoMining())
                .set(TopPlayer::getMcMmoRepair, topPlayer.getMcMmoRepair())
                .set(TopPlayer::getMcMmoSalvage, topPlayer.getMcMmoSalvage())
                .set(TopPlayer::getMcMmoSmelting, topPlayer.getMcMmoSmelting())
                .set(TopPlayer::getMcMmoSwords, topPlayer.getMcMmoSwords())
                .set(TopPlayer::getMcMmoTaming, topPlayer.getMcMmoTaming())
                .set(TopPlayer::getMcMmoUnarmed, topPlayer.getMcMmoUnarmed())
                .set(TopPlayer::getMcMmoWoodcutting, topPlayer.getMcMmoWoodcutting())
                .set(TopPlayer::getMcMmoArchery, topPlayer.getMcMmoArchery())
                .set(TopPlayer::getPlayerGuildKill, topPlayer.getPlayerGuildKill())
                .set(TopPlayer::getPlayerGuildDie, topPlayer.getPlayerGuildDie())
                .set(TopPlayer::getJobBrewer, topPlayer.getJobBrewer())
                .set(TopPlayer::getJobBuilder, topPlayer.getJobBuilder())
                .set(TopPlayer::getJobCrafter, topPlayer.getJobCrafter())
                .set(TopPlayer::getJobDigger, topPlayer.getJobDigger())
                .set(TopPlayer::getJobEnchanter, topPlayer.getJobEnchanter())
                .set(TopPlayer::getJobExplorer, topPlayer.getJobExplorer())
                .set(TopPlayer::getJobFarmer, topPlayer.getJobFarmer())
                .set(TopPlayer::getJobFisherman, topPlayer.getJobFisherman())
                .set(TopPlayer::getJobHunter, topPlayer.getJobHunter())
                .set(TopPlayer::getJobMiner, topPlayer.getJobMiner())
                .set(TopPlayer::getJobWeaponSmith, topPlayer.getJobWeaponSmith())
                .set(TopPlayer::getJobWoodcutter, topPlayer.getJobWoodcutter())
        ;
        use.where().eq(TopPlayer::getPlayerName, topPlayer.getPlayerName());
        use.execution().update();
    }

}