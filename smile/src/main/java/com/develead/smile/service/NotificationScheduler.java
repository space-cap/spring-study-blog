package com.develead.smile.service;
import com.develead.smile.domain.Appointment;
import com.develead.smile.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    // 매일 오전 9시에 실행
    @Scheduled(cron = "0 0 9 * * *")
    public void sendAppointmentReminders() {
        System.out.println("Sending daily appointment reminders...");
        LocalDateTime startOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime endOfTomorrow = startOfTomorrow.plusDays(1).minusNanos(1);

        List<Appointment> tomorrowAppointments = appointmentRepository.findAllByAppointmentDatetimeBetween(startOfTomorrow, endOfTomorrow);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (Appointment app : tomorrowAppointments) {
            notificationService.sendNotification(
                    app.getCustomer(),
                    "APPOINTMENT_REMINDER",
                    Map.of(
                            "고객명", app.getCustomer().getName(),
                            "병원명", app.getClinic().getClinicName(),
                            "예약일시", app.getAppointmentDatetime().format(formatter)
                    )
            );
        }
    }
}
