package com.patika.notificationservice.repo;

import com.patika.notificationservice.model.LogRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepo   extends JpaRepository <LogRecord, Long>{
}
