package com.patika.notificationservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.patika.notificationservice.dto.enums.LogType;
import com.patika.notificationservice.dto.enums.SuccessType;
import com.patika.notificationservice.model.LogRecord;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class NotificationDTO {

    private LogType logType;
    private SuccessType successType;
    private String databaseName;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime time;
    private String message;


    public LogRecord toLogRecord(){
        LogRecord logRecord = new LogRecord();
        logRecord.setLogType(logType.toString());
        logRecord.setSuccessType(successType.toString());
        logRecord.setTime(time);
        logRecord.setMessage(message);
        logRecord.setDbName(databaseName);

        return logRecord;
    }

}
