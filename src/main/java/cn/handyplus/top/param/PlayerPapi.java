package cn.handyplus.top.param;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author handy
 */
@Getter
@Setter
@Builder
public class PlayerPapi {

    private String playerName;

    private UUID playerUuid;

    private String papiType;

    private String papiValue;

}