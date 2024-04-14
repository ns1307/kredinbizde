package com.patika.notificationservice.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.patika.notificationservice.dto.enums.LogType;
import com.patika.notificationservice.dto.enums.SuccessType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "log-records")
public class LogRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logID;

    @Column(name = "log_type")
    private String logType;

    @Column(name = "db_name")
    private String dbName;

    @Column(name = "success")
    private String successType;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "message")
    private String message;

}
