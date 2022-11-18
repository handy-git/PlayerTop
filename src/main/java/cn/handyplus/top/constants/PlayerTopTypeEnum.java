package cn.handyplus.top.constants;

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
    VAULT(1, "金币", "vault", ""),
    PLAYER_POINTS(2, "点券", "playerPoints", ""),
    PLAYER_TITLE_COIN(3, "称号币", "playerTitleCoin", ""),
    PLAYER_TITLE_NUMBER(4, "称号数量", "playerTitleNumber", ""),
    PLAYER_TASK_COIN(5, "任务币", "playerTaskCoin", ""),
    PLAYER_GUILD_MONEY(6, "玩家公会贡献", "playerGuildMoney", ""),
    MC_MMO(7, "McMmo等级", "mcMmo", ""),
    MC_MMO_ACROBATICS(8, "McMmo杂技等级", "mcMmoAcrobatics", ""),
    MC_MMO_ALCHEMY(9, "McMmo炼金等级", "mcMmoAlchemy", ""),
    MC_MMO_ARCHERY(10, "McMmo箭术等级", "mcMmoArchery", ""),
    MC_MMO_AXES(11, "McMmo斧技等级", "mcMmoAxes", ""),
    MC_MMO_EXCAVATION(12, "McMmo挖掘等级", "mcMmoExcavation", ""),
    MC_MMO_FISHING(13, "McMmo钓鱼等级", "mcMmoFishing", ""),
    MC_MMO_HERBALISM(14, "McMmo草药学等级", "mcMmoHerbalism", ""),
    MC_MMO_MINING(15, "McMmo挖矿等级", "mcMmoMining", ""),
    MC_MMO_REPAIR(16, "McMmo修理等级", "mcMmoRepair", ""),
    MC_MMO_SALVAGE(17, "McMmo分解等级", "mcMmoSalvage", ""),
    MC_MMO_SMELTING(18, "McMmo冶炼等级", "mcMmoSmelting", ""),
    MC_MMO_SWORDS(19, "McMmo剑术等级", "mcMmoSwords", ""),
    MC_MMO_TAMING(20, "McMmo驯兽等级", "mcMmoTaming", ""),
    MC_MMO_UNARMED(21, "McMmo格斗等级", "mcMmoUnarmed", ""),
    MC_MMO_WOODCUTTING(22, "McMmo伐木等级", "mcMmoWoodcutting", ""),
    PLAYER_GUILD_KILL(23, "玩家公会战击杀", "playerGuildKill", ""),
    PLAYER_GUILD_DIE(24, "玩家公会战死亡", "playerGuildDie", ""),

    JOBS_BREWER(25, "Jobs 酿造师", "jobsBrewer", "Brewer"),
    JOBS_BUILDER(26, "Jobs 建筑家", "jobsBuilder", "Builder"),
    JOBS_CRAFTER(27, "Jobs 合成者", "jobsCrafter", "Crafter"),
    JOBS_DIGGER(28, "Jobs 挖掘者", "jobsDigger", "Digger"),
    JOBS_ENCHANTER(29, "Jobs 附魔师", "jobsEnchanter", "Enchanter"),
    JOBS_EXPLORER(30, "Jobs 探索者", "jobsExplorer", "Explorer"),
    JOBS_FARMER(31, "Jobs 农场主", "jobsFarmer", "Farmer"),
    JOBS_FISHERMAN(32, "Jobs 渔夫", "jobsFisherman", "Fisherman"),
    JOBS_HUNTER(33, "Jobs 猎人", "jobsHunter", "Hunter"),
    JOBS_MINER(34, "Jobs 矿工", "jobsMiner", "Miner"),
    JOBS_WEAPON_SMITH(35, "Jobs 武器商", "jobsWeaponSmith", "Weaponsmith"),
    JOBS_WOODCUTTER(36, "Jobs 伐木工", "jobsWoodcutter", "Woodcutter"),
    ;

    private final Integer id;
    private final String name;
    private final String type;
    private final String originalType;

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