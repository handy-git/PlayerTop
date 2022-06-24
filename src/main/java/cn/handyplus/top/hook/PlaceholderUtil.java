package cn.handyplus.top.hook;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.top.PlayerTop;
import cn.handyplus.top.constants.PlayerTopTypeEnum;
import cn.handyplus.top.enter.TopPlayer;
import cn.handyplus.top.service.TopPlayerService;
import cn.handyplus.top.util.ConfigUtil;
import cn.handyplus.top.util.TopUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.List;

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
        String type = placeholderStr[0];
        int pageNum = Integer.parseInt(placeholderStr[1]);

        PlayerTopTypeEnum topTypeEnum = PlayerTopTypeEnum.getType(type);
        if (topTypeEnum == null) {
            return "";
        }
        // 查询对应记录
        List<TopPlayer> list = TopPlayerService.getInstance().page(topTypeEnum, pageNum, 1);

        String content = "";
        if (CollUtil.isNotEmpty(list)) {
            String format = ConfigUtil.FORMAT_CONFIG.getString("format." + type, "");
            if (type.contains(PlayerTopTypeEnum.MC_MMO.getType())) {
                format = ConfigUtil.FORMAT_CONFIG.getString("format." + PlayerTopTypeEnum.MC_MMO.getType(), "");
            }
            content = TopUtil.getContent(topTypeEnum, format, list.get(0));
        }
        return plugin.getConfig().getString(placeholder, content);
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
