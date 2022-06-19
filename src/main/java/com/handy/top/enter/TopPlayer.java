package com.handy.top.enter;

import com.handy.lib.annotation.TableField;
import com.handy.lib.annotation.TableName;
import lombok.Data;

/**
 * 玩家排行数据
 *
 * @author handy
 */
@Data
@TableName(value = "top_player", comment = "玩家排行数据")
public class TopPlayer {

    @TableField(value = "id", comment = "ID")
    private Integer id;

    @TableField(value = "player_name", comment = "玩家名称", notNull = true)
    private String playerName;

    @TableField(value = "player_uuid", comment = "玩家uuid")
    private String playerUuid;

    @TableField(value = "op", comment = "是否op")
    private Boolean op;

    @TableField(value = "vault", comment = "金币", filedDefault = "0")
    private Double vault;

    @TableField(value = "player_points", comment = "点券", filedDefault = "0")
    private Integer playerPoints;

    @TableField(value = "player_title_coin", comment = "称号币数量", filedDefault = "0")
    private Integer playerTitleCoin;

    @TableField(value = "player_title_number", comment = "称号数量", filedDefault = "0")
    private Integer playerTitleNumber;

    @TableField(value = "player_task_coin", comment = "任务币数量", filedDefault = "0")
    private Integer playerTaskCoin;

    @TableField(value = "player_guild_money", comment = "玩家公会贡献", filedDefault = "0")
    private Integer playerGuildMoney;

    @TableField(value = "player_guild_kill", comment = "玩家公会战击杀", filedDefault = "0")
    private Integer playerGuildKill;

    @TableField(value = "player_guild_die", comment = "玩家公会战死亡", filedDefault = "0")
    private Integer playerGuildDie;

    @TableField(value = "mc_mmo_sum", comment = "McMmo 总等级", filedDefault = "0")
    private Integer mcMmoSum;

    @TableField(value = "mc_mmo_acrobatics", comment = "McMmo 杂技", filedDefault = "0")
    private Integer mcMmoAcrobatics;

    @TableField(value = "mc_mmo_alchemy", comment = "McMmo 炼金", filedDefault = "0")
    private Integer mcMmoAlchemy;

    @TableField(value = "mc_mmo_archery", comment = "McMmo 箭术", filedDefault = "0")
    private Integer mcMmoArchery;

    @TableField(value = "mc_mmo_axes", comment = "McMmo 斧技", filedDefault = "0")
    private Integer mcMmoAxes;

    @TableField(value = "mc_mmo_excavation", comment = "McMmo 挖掘", filedDefault = "0")
    private Integer mcMmoExcavation;

    @TableField(value = "mc_mmo_fishing", comment = "McMmo 钓鱼", filedDefault = "0")
    private Integer mcMmoFishing;

    @TableField(value = "mc_mmo_herbalism", comment = "McMmo 草药学", filedDefault = "0")
    private Integer mcMmoHerbalism;

    @TableField(value = "mc_mmo_mining", comment = "McMmo 挖矿", filedDefault = "0")
    private Integer mcMmoMining;

    @TableField(value = "mc_mmo_repair", comment = "McMmo 修理", filedDefault = "0")
    private Integer mcMmoRepair;

    @TableField(value = "mc_mmo_salvage", comment = "McMmo 分解", filedDefault = "0")
    private Integer mcMmoSalvage;

    @TableField(value = "mc_mmo_smelting", comment = "McMmo 冶炼", filedDefault = "0")
    private Integer mcMmoSmelting;

    @TableField(value = "mc_mmo_swords", comment = "McMmo 剑术", filedDefault = "0")
    private Integer mcMmoSwords;

    @TableField(value = "mc_mmo_taming", comment = "McMmo 驯兽", filedDefault = "0")
    private Integer mcMmoTaming;

    @TableField(value = "mc_mmo_unarmed", comment = "McMmo 格斗", filedDefault = "0")
    private Integer mcMmoUnarmed;

    @TableField(value = "mc_mmo_woodcutting", comment = "McMmo 伐木", filedDefault = "0")
    private Integer mcMmoWoodcutting;
}
