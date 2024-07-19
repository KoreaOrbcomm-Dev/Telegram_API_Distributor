package com.orbcomm.telegram_api_distributor.param.update;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class TelUpdateEntityParam {
    private int offset;
    private int length;
    private String type;
}
