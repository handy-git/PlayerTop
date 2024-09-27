package cn.handyplus.top.enter;

import cn.handyplus.lib.annotation.TableField;
import cn.handyplus.lib.annotation.TableName;
import cn.handyplus.lib.db.enums.IndexEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

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

    @TableField(value = "papi", comment = "变量类型", indexEnum = IndexEnum.INDEX)
    private String papi;

    @TableField(value = "sort", comment = "排序方式")
    private String sort;

    @TableField(value = "vault", comment = "值 vault 兼容历史数据不做变更")
    private BigDecimal value;

    @TableField(value = "rank", comment = "排名")
    private Integer rank;

    @TableField(value = "create_time", comment = "创建时间")
    private Date createTime;

    @TableField(value = "update_time", comment = "更新时间")
    private Date updateTime;

}