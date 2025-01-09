package com.supergroup.kos.building.domain.model.config.battle;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleTimeConfigDetail {
    private Long initDuration; // second
    private Long progressDuration; // second
    private Long breakDuration;  // second

    public Long getInitDuration() {
        if(Objects.nonNull(this.initDuration) && this.initDuration > 0) {
            return this.initDuration;
        }
        return 0L;
    }

    public Long getProgressDuration() {
        if(Objects.nonNull(this.progressDuration) && this.progressDuration > 0) {
            return this.progressDuration;
        }
        return 0L;
    }

    public Long getBreakDuration() {
        if(Objects.nonNull(this.breakDuration) && this.breakDuration > 0) {
            return this.breakDuration;
        }
        return 0L;
    }
}
