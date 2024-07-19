package com.orbcomm.telegram_api_distributor.param.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelUpdateMessageParam {
    private Long message_id;
    private TelUpdateFromChatParam from;
    private TelUpdateFromChatParam chat;
    private Long date;
    private String text;
    private List<TelUpdateEntityParam> entities;
}
