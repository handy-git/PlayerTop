package cn.handyplus.top.hook;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.enter.TopPapiPlayer;
import cn.handyplus.top.service.TopPapiPlayerService;
import cn.handyplus.top.util.ConfigUtil;
import cn.handyplus.top.util.TopUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

/**
 * 变量扩展
 *
 * @author handy
 */
public class PlaceholderUtil extends PlaceholderExpansion {
    private final PlayerTop plugin;

    public PlaceholderUtil(PlayerTop plugin) {
        this.plugin = plugin;
    }

    /**
     * 变量前缀
     *
     * @return 结果
     */
    @Override
    public String getIdentifier() {
        return "playerTop";
    }

    /**
     * 注册变量
     *
     * @param player      玩家
     * @param placeholder 变量字符串
     * @return 变量
     */
    @Override
    public String onRequest(OfflinePlayer player, String placeholder) {
        if (player == null) {
            return null;
        }
        String[] placeholderStr = placeholder.split("_");
        if (placeholderStr.length < 2) {
            return "";
        }
        // 处理后缀
        String suffix = placeholderStr[placeholderStr.length - 1];

        // 判断是当前玩家排行
        if ("rank".equals(suffix)) {
            String type = placeholder.replace("_" + suffix, "");
            Optional<TopPapiPlayer> topPapiPlayerOptional = TopPapiPlayerService.getInstance().findByUidAndType(player.getUniqueId().toString(), type);
            return topPapiPlayerOptional.map(topPapiPlayer -> topPapiPlayer.getRank().toString()).orElse("0");
        }

        // 判断是name
        boolean isName = "name".equals(suffix);
        String type;
        int pageNum;
        if (isName) {
            pageNum = Integer.parseInt(placeholderStr[placeholderStr.length - 2]);
            type = placeholder.replace("_" + pageNum + "_" + suffix, "");
        } else {
            pageNum = Integer.parseInt(suffix);
            type = placeholder.replace("_" + suffix, "");
        }
        // 判断是 内部变量/papi变量
        PlayerTopTypeEnum topTypeEnum = PlayerTopTypeEnum.getType(type);
        String originType = type;
        if (topTypeEnum != null) {
            type = topTypeEnum.getType();
        } else {
            type = "%" + type + "%";
        }
        // 查询对应记录
        Optional<TopPapiPlayer> topPapiPlayerOptional = TopPapiPlayerService.getInstance().findByRankAndType(pageNum, type);
        if (!topPapiPlayerOptional.isPresent()) {
            return "";
        }
        TopPapiPlayer topPapiPlayer = topPapiPlayerOptional.get();
        // 配置的格式化内容
        String format = ConfigUtil.FORMAT_CONFIG.getString("format." + originType, "");
        // McMmo特殊处理
        if (type.contains(PlayerTopTypeEnum.MC_MMO.getType())) {
            format = ConfigUtil.FORMAT_CONFIG.getString("format." + PlayerTopTypeEnum.MC_MMO.getType(), "");
        }
        // Jobs特殊处理
        if (type.contains("jobs")) {
            format = ConfigUtil.FORMAT_CONFIG.getString("format." + "jobs", "");
        }
        // 格式处理
        String content = StrUtil.isNotEmpty(format) ? TopUtil.getContent(format, topPapiPlayer) : topPapiPlayer.getRank().toString();
        return isName ? topPapiPlayer.getPlayerName() : content;
    }

    /**
     * 因为这是一个内部类，
     * 你必须重写这个方法，让PlaceholderAPI知道不要注销你的扩展类
     *
     * @return 结果
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * 因为这是一个内部类，所以不需要进行这种检查
     * 我们可以简单地返回{@code true}
     *
     * @return 结果
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * 作者
     *
     * @return 结果
     */
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * 版本
     *
     * @return 结果
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}
