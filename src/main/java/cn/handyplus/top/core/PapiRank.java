package cn.handyplus.top.core;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.service.TopPapiPlayerService;
import cn.handyplus.top.util.ConfigUtil;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        // 过滤黑名单
        List<String> blacklist = ConfigUtil.CONFIG.getStringList("blacklist");
        // 过滤OP
        if (ConfigUtil.CONFIG.getBoolean("isOp")) {
            blacklist.addAll(AsyncTask.getOpUidList());
        }
        // 过滤值
        List<Long> filter = ConfigUtil.CONFIG.getLongList("filter");
        List<BigDecimal> filterList = filter.stream().map(BigDecimal::valueOf).collect(Collectors.toList());
        // 分组处理 避免bc配置不同
        Map<String, List<TopPapiPlayer>> topPapiPlayerGroupList = topPapiPlayerList.stream().collect(Collectors.groupingBy(TopPapiPlayer::getPapi));
        for (String papi : topPapiPlayerGroupList.keySet()) {
            papiRank(sender, papi, topPapiPlayerGroupList.get(papi), blacklist, filterList);
        }
    }

    /**
     * 处理值和排行
     */
    private static void papiRank(CommandSender sender, String papi, List<TopPapiPlayer> papiList, List<String> blacklist, List<BigDecimal> filterList) {
        long start = System.currentTimeMillis();
        // 1. 先查询现有数据
        List<TopPapiPlayer> dbTopList = TopPapiPlayerService.getInstance().findByPapi(papi);
        Map<String, TopPapiPlayer> dbPapiMap = dbTopList.stream().collect(Collectors.toMap(TopPapiPlayer::getPlayerUuid, e -> e));
        // 2. 更新数据
        List<TopPapiPlayer> newList = new ArrayList<>();
        for (TopPapiPlayer topPapi : papiList) {
            TopPapiPlayer dbTopPapiPlayer = dbPapiMap.get(topPapi.getPlayerUuid());
            if (dbTopPapiPlayer == null) {
                newList.add(topPapi);
            } else {
                dbTopPapiPlayer.setVault(topPapi.getVault());
                dbTopPapiPlayer.setPlayerName(topPapi.getPlayerName());
                newList.add(dbTopPapiPlayer);
            }
        }
        // 3. 过滤不需要的数据
        newList = newList.stream().filter(topPapiPlayer -> !blacklist.contains(topPapiPlayer.getPlayerName())).collect(Collectors.toList());
        newList = newList.stream().filter(topPapiPlayer -> !filterList.contains(topPapiPlayer.getVault())).collect(Collectors.toList());
        // 4. 处理排序
        if ("desc".equalsIgnoreCase(papiList.get(0).getSort())) {
            newList = newList.stream().peek(player -> player.setSort("desc")).collect(Collectors.toList());
            newList = newList.stream().sorted(Comparator.comparing(TopPapiPlayer::getVault).reversed()).collect(Collectors.toList());
        } else {
            newList = newList.stream().peek(player -> player.setSort("asc")).collect(Collectors.toList());
            newList = newList.stream().sorted(Comparator.comparing(TopPapiPlayer::getVault)).collect(Collectors.toList());
        }
        // 更新排序
        for (int i = 0; i < newList.size(); i++) {
            newList.get(i).setRank(i + 1);
        }
        TopPapiPlayerService.getInstance().setVault(newList);
        MessageUtil.sendMessage(sender, "2.5 -> 同步" + papi + "变量数据结束" + ",同步消耗:" + (System.currentTimeMillis() - start) / 1000 + "秒,当前进度: 2.5/6");
    }

}
