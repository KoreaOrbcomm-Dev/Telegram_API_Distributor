package com.orbcomm.telegram_api_distributor.param.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class TelUpdateFromChatParam {
    private Long id;
    private Boolean is_bot;
    private String first_name;
    private String language_code;
    private String type;
}
