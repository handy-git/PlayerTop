package com.handy.top.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家数据类型
 *
 * @author handy
 */
@Getter
@AllArgsConstructor
public enum PlayerTopTypeEnum {
    /**
     * 数据类型
     */
    VAULT(1, "金币", "vault"),
    PLAYER_POINTS(2, "点券", "player_points"),
    PLAYER_TITLE_COIN(3, "称号币", "player_title_coin"),
    PLAYER_TITLE_NUMBER(4, "称号数量", "player_title_number"),
    PLAYER_TASK_COIN(5, "任务币", "player_task_coin"),
    PLAYER_GUILD_MONEY(5, "玩家公会贡献", "player_guild_money"),
    ;

    private final Integer id;
    private final String name;
    private final String type;

    /**
     * 根据类型获取
     *
     * @param type 类型
     * @return GuildActivityType
     */
    public static PlayerTopTypeEnum getType(String type) {
        for (PlayerTopTypeEnum guildActivityType : PlayerTopTypeEnum.values()) {
            if (guildActivityType.getType().equals(type)) {
                return guildActivityType;
            }
        }
        return null;
    }

    /**
     * 获取全部类型
     *
     * @return GuildActivityType
     */
    public static List<String> getTypeList() {
        List<String> typeList = new ArrayList<>();
        for (PlayerTopTypeEnum guildActivityType : PlayerTopTypeEnum.values()) {
            typeList.add(guildActivityType.getType());
        }
        return typeList;
    }

}