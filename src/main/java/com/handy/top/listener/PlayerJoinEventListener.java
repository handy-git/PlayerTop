package com.handy.top.listener;

import com.handy.lib.annotation.HandyListener;
import com.handy.lib.constants.BaseConstants;
import com.handy.lib.util.HandyHttpUtil;
import com.handy.top.constants.TopConstants;
import com.handy.top.util.ConfigUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * 玩家尝试登录的事件
 *
 * @author handy
 */
@HandyListener
public class PlayerJoinEventListener implements Listener {

    /**
     * op进入服务器发送更新提醒
     *
     * @param event 事件
     */
    @EventHandler
    public void onOpPlayerJoin(PlayerJoinEvent event) {
        // op登录发送更新提醒
        if (!ConfigUtil.CONFIG.getBoolean(BaseConstants.IS_CHECK_UPDATE_TO_OP_MSG)) {
            return;
        }
        HandyHttpUtil.checkVersion(event.getPlayer(), TopConstants.PLUGIN_VERSION_URL);
    }

}
