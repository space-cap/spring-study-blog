package com.develead.smile.service;
import com.develead.smile.domain.PaymentTransaction;
import com.develead.smile.repository.PaymentTransactionRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PaymentTransactionRepository transactionRepository;
    private final SpringTemplateEngine templateEngine;

    public byte[] generateDailySalesReport(LocalDate date) throws IOException {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<PaymentTransaction> transactions = transactionRepository.findAllByTransactionDateBetween(start, end);

        String title = "일일 매출 보고서";
        String dateRange = date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        return generatePdfFromTransactions(title, dateRange, transactions);
    }

    public byte[] generateWeeklySalesReport(LocalDate date) throws IOException {
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);

        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = endOfWeek.atTime(LocalTime.MAX);
        List<PaymentTransaction> transactions = transactionRepository.findAllByTransactionDateBetween(start, end);

        String title = "주간 매출 보고서";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String dateRange = String.format("%s ~ %s (%d년 %d주차)",
                startOfWeek.format(formatter),
                endOfWeek.format(formatter),
                startOfWeek.getYear(),
                startOfWeek.get(WeekFields.of(Locale.KOREA).weekOfYear()));

        return generatePdfFromTransactions(title, dateRange, transactions);
    }

    public byte[] generateMonthlySalesReport(YearMonth yearMonth) throws IOException {
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        List<PaymentTransaction> transactions = transactionRepository.findAllByTransactionDateBetween(start, end);

        String title = "월간 매출 보고서";
        String dateRange = yearMonth.format(DateTimeFormatter.ofPattern("yyyy년 MM월"));

        return generatePdfFromTransactions(title, dateRange, transactions);
    }

    private byte[] generatePdfFromTransactions(String title, String dateRange, List<PaymentTransaction> transactions) throws IOException {
        BigDecimal totalRevenue = transactions.stream()
                .map(PaymentTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> revenueByMethod = transactions.stream()
                .collect(Collectors.groupingBy(
                        PaymentTransaction::getPaymentMethod,
                        Collectors.reducing(BigDecimal.ZERO, PaymentTransaction::getAmount, BigDecimal::add)
                ));

        Context context = new Context();
        context.setVariable("reportTitle", title);
        context.setVariable("reportDateRange", dateRange);
        context.setVariable("transactions", transactions);
        context.setVariable("totalRevenue", totalRevenue);
        context.setVariable("revenueByMethod", revenueByMethod);

        String html = templateEngine.process("reports/sales-report-template", context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            File fontFile = new File("src/main/resources/fonts/NanumGothic-Regular.ttf");
            if (fontFile.exists()) {
                builder.useFont(fontFile, "NanumGothic");
            } else {
                System.err.println("Font file not found at: " + fontFile.getAbsolutePath());
            }

            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }
}
