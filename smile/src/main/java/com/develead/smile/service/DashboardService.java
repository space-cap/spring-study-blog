package com.develead.smile.service;
import com.develead.smile.dto.DashboardDto;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final AppointmentRepository appointmentRepository;
    private final BillingRepository billingRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final CustomerRepository customerRepository;

    public DashboardDto getDashboardStatistics() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        long todayAppointments = appointmentRepository.countByAppointmentDatetimeBetween(startOfDay, endOfDay);

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);
        BigDecimal monthlyRevenue = billingRepository.findTotalRevenueBetween(startOfMonth, endOfMonth);

        long lowStockItems = inventoryItemRepository.countLowStockItems();
        long totalCustomers = customerRepository.count();

        return DashboardDto.builder()
                .todayAppointments(todayAppointments)
                .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
                .lowStockItems(lowStockItems)
                .totalCustomers(totalCustomers)
                .build();
    }
}
