package com.develead.smile.dto;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class DashboardDto {
    private long todayAppointments;
    private BigDecimal monthlyRevenue;
    private long lowStockItems;
    private long totalCustomers;
}
