package cn.handyplus.top.core;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.db.Tx;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.service.TopPapiPlayerService;
import cn.handyplus.top.util.ConfigUtil;
import org.bukkit.command.CommandSender;

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
        List<Long> filterList = ConfigUtil.CONFIG.getLongList("filter");
        // 分组处理 避免bc配置不同
        Map<String, List<TopPapiPlayer>> topPapiPlayerGroupList = topPapiPlayerList.stream().collect(Collectors.groupingBy(TopPapiPlayer::getPapi));
        for (String papi : topPapiPlayerGroupList.keySet()) {
            // 事物处理
            Tx.use().tx(tx -> papiRank(sender, papi, topPapiPlayerGroupList.get(papi), blacklist, filterList));
        }
    }

    /**
     * 处理值和排行
     */
    private static void papiRank(CommandSender sender, String papi, List<TopPapiPlayer> papiList, List<String> blacklist, List<Long> filterList) {
        long start = System.currentTimeMillis();
        MessageUtil.sendMessage(sender, "2.5 -> 同步" + papi + "变量数据开始,当前进度: 2.5/6");
        // 1. 先更新本次同步的数据
        for (TopPapiPlayer topPapiPlayer : papiList) {
            TopPapiPlayerService.getInstance().setVault(topPapiPlayer);
        }
        // 2. 过滤不需要的数据
        TopPapiPlayerService.getInstance().deleteByNameAndPapi(blacklist, papi);
        TopPapiPlayerService.getInstance().deleteByValueAndPapi(filterList, papi);
        // 3. 处理排序
        List<TopPapiPlayer> dbTopList = TopPapiPlayerService.getInstance().findByPapi(papi);
        // 判断排序类型
        if ("desc".equalsIgnoreCase(papiList.get(0).getSort())) {
            dbTopList = dbTopList.stream().peek(player -> player.setSort("desc")).collect(Collectors.toList());
            dbTopList = dbTopList.stream().sorted(Comparator.comparing(TopPapiPlayer::getVault).reversed()).collect(Collectors.toList());
        } else {
            dbTopList = dbTopList.stream().peek(player -> player.setSort("asc")).collect(Collectors.toList());
            dbTopList = dbTopList.stream().sorted(Comparator.comparing(TopPapiPlayer::getVault)).collect(Collectors.toList());
        }
        // 更新排序
        for (int i = 0; i < dbTopList.size(); i++) {
            TopPapiPlayer topPapiPlayer = dbTopList.get(i);
            TopPapiPlayerService.getInstance().updateRank(topPapiPlayer.getId(), i + 1);
        }
        MessageUtil.sendMessage(sender, "2.5 -> 同步" + papi + "变量数据结束" + ",同步消耗:" + (System.currentTimeMillis() - start) / 1000 + "秒,当前进度: 2.5/6");
    }

}
