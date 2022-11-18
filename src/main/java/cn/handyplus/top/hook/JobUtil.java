package cn.handyplus.top.hook;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.top.PlayerTop;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author handy
 */
public class JobUtil {
    private JobUtil() {
    }

    public static JobUtil getInstance() {
        return JobUtil.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final JobUtil INSTANCE = new JobUtil();
    }

    /**
     * 获取玩家职业等级
     *
     * @param playerName 玩家
     * @return 等级map key 职业名 value 等级
     */
    public Map<String, Integer> getLevelMap(String playerName) {
        Map<String, Integer> map = new HashMap<>();
        if (!PlayerTop.USE_JOB || StrUtil.isEmpty(playerName)) {
            return map;
        }
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(playerName);
        if (jobsPlayer == null) {
            return map;
        }
        List<JobProgression> jobs = jobsPlayer.getJobProgression();
        for (JobProgression jobProgression : jobs) {
            map.put(jobProgression.getJob().getJobFullName(), jobProgression.getLevel());
        }
        return map;
    }

}
