package cn.handyplus.top.hook;

import cn.handyplus.top.PlayerTop;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
     * @param playerUuid 玩家uid
     * @return 等级map key 职业名 value 等级
     */
    public Map<String, Long> getLevelMap(UUID playerUuid) {
        Map<String, Long> map = new HashMap<>();
        if (!PlayerTop.USE_JOB || playerUuid == null) {
            return map;
        }
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(playerUuid);
        if (jobsPlayer == null) {
            return map;
        }
        List<JobProgression> jobs = jobsPlayer.getJobProgression();
        for (JobProgression jobProgression : jobs) {
            map.put(jobProgression.getJob().getName(), (long) jobProgression.getLevel());
        }
        return map;
    }

}
