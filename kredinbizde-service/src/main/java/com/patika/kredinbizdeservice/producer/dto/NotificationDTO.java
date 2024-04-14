package com.patika.kredinbizdeservice.producer.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.patika.kredinbizdeservice.producer.enums.LogType;
import com.patika.kredinbizdeservice.producer.enums.SuccessType;
import lombok.*;

import java.time.LocalDate;
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
