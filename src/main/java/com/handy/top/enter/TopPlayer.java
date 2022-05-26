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
}
