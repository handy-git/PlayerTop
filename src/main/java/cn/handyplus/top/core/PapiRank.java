package cn.handyplus.top.core;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.service.TopPapiPlayerService;
import cn.handyplus.top.util.ConfigUtil;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 处理更新排行
 *
 * @author handy
 * @since 1.5.1
 */
public class PapiRank {

    /**
     * 批量新增或更新数据
     *
     * @param sender            控制台
     * @param topPapiPlayerList 批量记录
     */
    public static void replace(CommandSender sender, List<TopPapiPlayer> topPapiPlayerList) {
        if (CollUtil.isEmpty(topPapiPlayerList)) {
            return;
        }
        // 分组处理 避免bc配置不同
        Map<String, List<TopPapiPlayer>> topPapiPlayerGroupList = topPapiPlayerList.stream().collect(Collectors.groupingBy(TopPapiPlayer::getPapi));
        for (String papi : topPapiPlayerGroupList.keySet()) {
            papiRank(sender, papi, topPapiPlayerGroupList.get(papi));
        }
    }

    /**
     * 处理值和排行
     */
    private static void papiRank(CommandSender sender, String papi, List<TopPapiPlayer> papiList) {
        Date now = new Date();
        // 过滤黑名单
        List<String> blacklist = ConfigUtil.CONFIG.getStringList("blacklist");
        int blacklistNum = TopPapiPlayerService.getInstance().deleteByPlayerName(blacklist, papi);
        MessageUtil.sendMessage(sender, "2.5 -> 已过滤黑名单玩家" + blacklistNum + "个,当前进度: 2.5/6");
        // 过滤OP
        int opUidListNum = TopPapiPlayerService.getInstance().deleteByPlayerUuid(AsyncTask.getOpUidList(), papi);
        MessageUtil.sendMessage(sender, "2.5 -> 已过滤OP玩家" + opUidListNum + "个,当前进度: 2.5/6");
        // 过滤值
        List<Long> filter = ConfigUtil.CONFIG.getLongList("filter");
        List<BigDecimal> filterList = filter.stream().map(BigDecimal::valueOf).collect(Collectors.toList());
        int filterListNum = TopPapiPlayerService.getInstance().deleteByValue(filterList, papi);
        MessageUtil.sendMessage(sender, "2.5 -> 已过滤值" + filterListNum + "个,当前进度: 2.5/6");
        // 开始同步数据
        long start = System.currentTimeMillis();
        // 1. 先查询现有数据
        List<TopPapiPlayer> dbTopList = TopPapiPlayerService.getInstance().findByPapi(papi);
        Set<TopPapiPlayer> dbTopSet = new HashSet<>(dbTopList);
        Map<String, TopPapiPlayer> papiListMap = papiList.stream().collect(Collectors.toMap(TopPapiPlayer::getPlayerUuid, e -> e));
        // 更新旧记录
        for (TopPapiPlayer dbTop : dbTopList) {
            TopPapiPlayer topPapiPlayer = papiListMap.get(dbTop.getPlayerUuid());
            if (topPapiPlayer != null) {
                dbTop.setValue(topPapiPlayer.getValue());
                dbTop.setUpdateTime(now);
                dbTop.setPlayerName(topPapiPlayer.getPlayerName());
            }
        }
        // 添加新记录
        Set<String> dbPlayerSet = dbTopList.stream().map(TopPapiPlayer::getPlayerUuid).collect(Collectors.toSet());
        List<TopPapiPlayer> differenceFromPapi = papiList.stream().filter(p -> !dbPlayerSet.contains(p.getPlayerUuid())).collect(Collectors.toList());
        dbTopList.addAll(differenceFromPapi);
        // 4. 处理排序
        if ("desc".equalsIgnoreCase(papiList.get(0).getSort())) {
            dbTopList = dbTopList.stream().peek(player -> player.setSort("desc")).collect(Collectors.toList());
            dbTopList = dbTopList.stream().sorted(Comparator.comparing(TopPapiPlayer::getValue).reversed()).collect(Collectors.toList());
        } else {
            dbTopList = dbTopList.stream().peek(player -> player.setSort("asc")).collect(Collectors.toList());
            dbTopList = dbTopList.stream().sorted(Comparator.comparing(TopPapiPlayer::getValue)).collect(Collectors.toList());
        }
        // 更新排序
        for (int i = 0; i < dbTopList.size(); i++) {
            dbTopList.get(i).setRank(i + 1);
        }
        // 本次需要处理的数据
        List<TopPapiPlayer> newList = dbTopList.stream().filter(obj -> !dbTopSet.contains(obj)).collect(Collectors.toList());
        TopPapiPlayerService.getInstance().setValue(newList);
        MessageUtil.sendMessage(sender, "2.5 -> 同步" + papi + "变量数据" + newList.size() + "条结束" + ",同步消耗:" + (System.currentTimeMillis() - start) / 1000 + "秒,当前进度: 2.5/6");
    }

}
