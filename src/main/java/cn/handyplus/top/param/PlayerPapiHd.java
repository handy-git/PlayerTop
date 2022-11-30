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
}
