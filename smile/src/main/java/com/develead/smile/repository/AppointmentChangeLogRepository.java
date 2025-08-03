package com.develead.smile.repository;
import com.develead.smile.domain.AppointmentChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppointmentChangeLogRepository extends JpaRepository<AppointmentChangeLog, Integer> {}