package com.orbcomm.telegram_api_distributor.param.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelUpdateResultParam {
    private Long update_id;
    private TelUpdateMessageParam message;
}
