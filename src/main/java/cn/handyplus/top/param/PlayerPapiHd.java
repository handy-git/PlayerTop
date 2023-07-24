package cn.handyplus.top.param;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;

/**
 * @author handy
 */
@Getter
@Setter
@Builder
public class PlayerPapiHd {

    private List<String> textLineList;

    private Location location;

    private String material;

    /**
     * 自定义材质
     *
     * @since 1.3.3
     */
    private int customModelData;
}
