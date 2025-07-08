package com.develead.denti.service;

import com.develead.denti.dto.TreatmentPlanDto;
import com.develead.denti.dto.TreatmentItemDto;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PdfService {
    
    private final TemplateEngine templateEngine;
    private final TreatmentPlanService treatmentPlanService;
    
    public byte[] generateTreatmentPlanPdf(Long treatmentPlanId) throws IOException {
        TreatmentPlanDto treatmentPlan = treatmentPlanService.getTreatmentPlanById(treatmentPlanId)
                .orElseThrow(() -> new RuntimeException("Treatment plan not found"));
        
        Context context = new Context(Locale.KOREAN);
        context.setVariable("treatmentPlan", treatmentPlan);
        context.setVariable("patient", treatmentPlan);
        context.setVariable("treatmentItems", treatmentPlan.getTreatmentItems());
        context.setVariable("toothCharts", treatmentPlan.getToothCharts());
        context.setVariable("dateFormatter", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        context.setVariable("totalCost", treatmentPlan.getTotalCost() != null ? treatmentPlan.getTotalCost() : BigDecimal.ZERO);
        context.setVariable("insuranceCost", treatmentPlan.getInsuranceCost() != null ? treatmentPlan.getInsuranceCost() : BigDecimal.ZERO);
        context.setVariable("selfCost", treatmentPlan.getSelfCost() != null ? treatmentPlan.getSelfCost() : BigDecimal.ZERO);
        
        String htmlContent = templateEngine.process("pdf/treatment-plan", context);
        
        return convertHtmlToPdf(htmlContent);
    }
    
    public byte[] generateBillPdf(Long treatmentPlanId) throws IOException {
        TreatmentPlanDto treatmentPlan = treatmentPlanService.getTreatmentPlanById(treatmentPlanId)
                .orElseThrow(() -> new RuntimeException("Treatment plan not found"));
        
        Context context = new Context(Locale.KOREAN);
        context.setVariable("treatmentPlan", treatmentPlan);
        context.setVariable("patient", treatmentPlan);
        context.setVariable("treatmentItems", treatmentPlan.getTreatmentItems());
        context.setVariable("dateFormatter", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        context.setVariable("totalCost", treatmentPlan.getTotalCost() != null ? treatmentPlan.getTotalCost() : BigDecimal.ZERO);
        context.setVariable("insuranceCost", treatmentPlan.getInsuranceCost() != null ? treatmentPlan.getInsuranceCost() : BigDecimal.ZERO);
        context.setVariable("selfCost", treatmentPlan.getSelfCost() != null ? treatmentPlan.getSelfCost() : BigDecimal.ZERO);
        
        String htmlContent = templateEngine.process("pdf/bill", context);
        
        return convertHtmlToPdf(htmlContent);
    }
    
    private byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDocument = new PdfDocument(writer)) {
            
            //HtmlConverter.convertToPdf(htmlContent, pdfDocument);
        }
        
        return outputStream.toByteArray();
    }
}

