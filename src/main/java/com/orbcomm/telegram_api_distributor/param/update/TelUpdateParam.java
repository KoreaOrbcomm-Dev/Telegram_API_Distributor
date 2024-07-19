package com.orbcomm.telegram_api_distributor.param.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelUpdateParam {
    private Boolean ok;
    private List<TelUpdateResultParam> result;
}
