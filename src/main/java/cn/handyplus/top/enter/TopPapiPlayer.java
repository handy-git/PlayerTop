package cn.handyplus.top.enter;

import cn.handyplus.lib.annotation.TableField;
import cn.handyplus.lib.annotation.TableName;
import cn.handyplus.lib.db.enums.IndexEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 玩家Papi排行数据
 *
 * @author handy
 */
@Getter
@Setter
@TableName(value = "top_papi_player", comment = "玩家Papi排行数据")
public class TopPapiPlayer {

    @TableField(value = "id", comment = "ID")
    private Integer id;

    @TableField(value = "player_name", comment = "玩家名称")
    private String playerName;

    @TableField(value = "player_uuid", comment = "玩家uuid", notNull = true, indexEnum = IndexEnum.INDEX)
    private String playerUuid;

    @TableField(value = "op", comment = "是否op")
    private Boolean op;

    @TableField(value = "papi", comment = "变量类型", indexEnum = IndexEnum.INDEX)
    private String papi;

    @TableField(value = "vault", comment = "值")
    private Integer vault;

    @TableField(value = "rank", comment = "排名")
    private Integer rank;

}