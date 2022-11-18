package cn.handyplus.top.param;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author handy
 */
@Getter
@Setter
@Builder
public class PlayerPapi {

    private String playerName;

    private String papiType;

    private String papiValue;
}
