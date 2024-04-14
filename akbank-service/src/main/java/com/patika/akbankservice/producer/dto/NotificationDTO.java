package com.patika.akbankservice.producer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.patika.akbankservice.producer.enums.LogType;
import com.patika.akbankservice.producer.enums.SuccessType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private LogType logType;
    private SuccessType successType;
    private String databaseName;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime time;
    private String message;

}
