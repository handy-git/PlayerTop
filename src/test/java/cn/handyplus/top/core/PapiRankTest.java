package cn.handyplus.top.core;

import cn.handyplus.lib.core.DateUtil;
import cn.handyplus.top.enter.TopPapiPlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author handy
 */
public class PapiRankTest {

    @Test
    public void testDescSorted() {
        List<TopPapiPlayer> dbTopList = new ArrayList<>();

        String sort = "desc";

        TopPapiPlayer player1 = new TopPapiPlayer();
        player1.setId(1);
        player1.setSort(sort);
        player1.setValue(BigDecimal.valueOf(10));
        // 10秒前
        player1.setCreateTime(new Date(System.currentTimeMillis() - 10000));
        dbTopList.add(player1);

        TopPapiPlayer player2 = new TopPapiPlayer();
        player2.setId(2);
        player2.setSort(sort);
        player2.setValue(BigDecimal.valueOf(15));
        // 5秒前
        player2.setCreateTime(new Date(System.currentTimeMillis() - 5000));
        dbTopList.add(player2);

        TopPapiPlayer player3 = new TopPapiPlayer();
        player3.setId(3);
        player3.setSort(sort);
        player3.setValue(BigDecimal.valueOf(10));
        // 20秒前
        player3.setCreateTime(new Date(System.currentTimeMillis() - 20000));
        dbTopList.add(player3);

        TopPapiPlayer player4 = new TopPapiPlayer();
        player4.setId(4);
        player4.setSort(sort);
        player4.setValue(BigDecimal.valueOf(20));
        player4.setCreateTime(new Date()); // 当前时间
        dbTopList.add(player4);

        TopPapiPlayer player5 = new TopPapiPlayer();
        player5.setId(5);
        player5.setSort(sort);
        player5.setValue(BigDecimal.valueOf(15));
        // 15秒前
        player5.setCreateTime(new Date(System.currentTimeMillis() - 15000));
        dbTopList.add(player5);

        // 4. 处理排序
        if ("desc".equalsIgnoreCase(dbTopList.get(0).getSort())) {
            dbTopList = dbTopList.stream().peek(player -> player.setSort("desc")).collect(Collectors.toList());
            dbTopList = dbTopList.stream().sorted(Comparator.comparing(TopPapiPlayer::getValue).reversed().thenComparing(TopPapiPlayer::getCreateTime)).collect(Collectors.toList());
        } else {
            dbTopList = dbTopList.stream().peek(player -> player.setSort("asc")).collect(Collectors.toList());
            dbTopList = dbTopList.stream().sorted(Comparator.comparing(TopPapiPlayer::getValue).thenComparing(TopPapiPlayer::getCreateTime)).collect(Collectors.toList());
        }
        dbTopList.forEach(s -> System.out.println("id:" + s.getId() + ",value:" + s.getValue() + ",date:" + DateUtil.format(s.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
        Assertions.assertEquals(4, dbTopList.get(0).getId());
        Assertions.assertEquals(5, dbTopList.get(1).getId());
        Assertions.assertEquals(2, dbTopList.get(2).getId());
        Assertions.assertEquals(3, dbTopList.get(3).getId());
        Assertions.assertEquals(1, dbTopList.get(4).getId());
    }

    @Test
    public void testAscSorted() {
        List<TopPapiPlayer> dbTopList = new ArrayList<>();

        String sort = "asc";

        TopPapiPlayer player1 = new TopPapiPlayer();
        player1.setId(1);
        player1.setSort(sort);
        player1.setValue(BigDecimal.valueOf(10));
        // 10秒前
        player1.setCreateTime(new Date(System.currentTimeMillis() - 10000));
        dbTopList.add(player1);

        TopPapiPlayer player2 = new TopPapiPlayer();
        player2.setId(2);
        player2.setSort(sort);
        player2.setValue(BigDecimal.valueOf(15));
        // 5秒前
        player2.setCreateTime(new Date(System.currentTimeMillis() - 5000));
        dbTopList.add(player2);

        TopPapiPlayer player3 = new TopPapiPlayer();
        player3.setId(3);
        player3.setSort(sort);
        player3.setValue(BigDecimal.valueOf(10));
        // 20秒前
        player3.setCreateTime(new Date(System.currentTimeMillis() - 20000));
        dbTopList.add(player3);

        TopPapiPlayer player4 = new TopPapiPlayer();
        player4.setId(4);
        player4.setSort(sort);
        player4.setValue(BigDecimal.valueOf(20));
        player4.setCreateTime(new Date()); // 当前时间
        dbTopList.add(player4);

        TopPapiPlayer player5 = new TopPapiPlayer();
        player5.setId(5);
        player5.setSort(sort);
        player5.setValue(BigDecimal.valueOf(15));
        // 15秒前
        player5.setCreateTime(new Date(System.currentTimeMillis() - 15000));
        dbTopList.add(player5);

        // 4. 处理排序
        if ("desc".equalsIgnoreCase(dbTopList.get(0).getSort())) {
            dbTopList = dbTopList.stream().peek(player -> player.setSort("desc")).collect(Collectors.toList());
            dbTopList = dbTopList.stream().sorted(Comparator.comparing(TopPapiPlayer::getValue).reversed().thenComparing(TopPapiPlayer::getCreateTime)).collect(Collectors.toList());
        } else {
            dbTopList = dbTopList.stream().peek(player -> player.setSort("asc")).collect(Collectors.toList());
            dbTopList = dbTopList.stream().sorted(Comparator.comparing(TopPapiPlayer::getValue).thenComparing(TopPapiPlayer::getCreateTime)).collect(Collectors.toList());
        }
        dbTopList.forEach(s -> System.out.println("id:" + s.getId() + ",value:" + s.getValue() + ",date:" + DateUtil.format(s.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
        Assertions.assertEquals(3, dbTopList.get(0).getId());
        Assertions.assertEquals(1, dbTopList.get(1).getId());
        Assertions.assertEquals(5, dbTopList.get(2).getId());
        Assertions.assertEquals(2, dbTopList.get(3).getId());
        Assertions.assertEquals(4, dbTopList.get(4).getId());
    }

}
