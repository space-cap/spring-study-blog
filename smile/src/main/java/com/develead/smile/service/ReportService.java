package com.develead.smile.service;
import com.develead.smile.domain.PaymentTransaction;
import com.develead.smile.repository.PaymentTransactionRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PaymentTransactionRepository transactionRepository;
    private final SpringTemplateEngine templateEngine;

    public byte[] generateDailySalesReport(LocalDate date) throws IOException {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PaymentTransaction> transactions = transactionRepository.findAllByTransactionDateBetween(startOfDay, endOfDay);

        BigDecimal totalRevenue = transactions.stream()
                .map(PaymentTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> revenueByMethod = transactions.stream()
                .collect(Collectors.groupingBy(
                        PaymentTransaction::getPaymentMethod,
                        Collectors.reducing(BigDecimal.ZERO, PaymentTransaction::getAmount, BigDecimal::add)
                ));

        Context context = new Context();
        context.setVariable("reportDate", date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        context.setVariable("transactions", transactions);
        context.setVariable("totalRevenue", totalRevenue);
        context.setVariable("revenueByMethod", revenueByMethod);

        String html = templateEngine.process("reports/daily-sales-template", context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            // 한글 폰트 지원을 위해 폰트 파일이 필요합니다. (프로젝트에 폰트 파일 추가 필요)
            // 예: builder.useFont(new File("src/main/resources/fonts/NanumGothic.ttf"), "NanumGothic");
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }
}
