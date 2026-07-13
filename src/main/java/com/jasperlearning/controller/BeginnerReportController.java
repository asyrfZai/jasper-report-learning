package com.jasperlearning.controller;

import com.jasperlearning.report.GeneratedReport;
import com.jasperlearning.report.JasperReportService;
import com.jasperlearning.report.ReportFormat;
import com.jasperlearning.util.ReportHttpSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * BEGINNER TIER
 * =============
 * Every endpoint here uses JasperReportService#renderStatic, meaning the
 * report has no repeating data source rows - all it needs is the template
 * path plus (optionally) a handful of report PARAMETERS. This is the
 * simplest possible controller <-> report wiring and deliberately mirrors
 * how a learner would hand-test each template from a browser URL.
 */
@RestController
@RequiredArgsConstructor
public class BeginnerReportController {

    private final JasperReportService jasperReportService;

    /** http://localhost:8080/api/reports/beginner/hello-world?format=pdf */
    @GetMapping("/api/reports/beginner/hello-world")
    public ResponseEntity<byte[]> helloWorld(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderStatic(
                "reports/beginner/01_hello_world.jrxml", null, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "hello-world");
    }

    /** http://localhost:8080/api/reports/beginner/certificate?format=pdf */
    @GetMapping("/api/reports/beginner/certificate")
    public ResponseEntity<byte[]> staticCertificate(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderStatic(
                "reports/beginner/02_static_certificate.jrxml", null, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "certificate");
    }

    /**
     * http://localhost:8080/api/reports/beginner/greeting?name=Ada&course=JasperReports&score=95&format=pdf
     * Demonstrates: every query param becomes a report PARAMETER (see
     * 03_parameters_greeting.jrxml's <parameter> declarations and their
     * defaultValueExpression, which kick in if you omit a query param).
     */
    @GetMapping("/api/reports/beginner/greeting")
    public ResponseEntity<byte[]> parametersGreeting(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Integer score,
            @RequestParam(defaultValue = "pdf") String format) {

        Map<String, Object> params = new HashMap<>();
        if (name != null) params.put("recipientName", name);
        if (course != null) params.put("courseName", course);
        if (score != null) params.put("score", score);
        params.put("generatedOn", new Date());

        GeneratedReport report = jasperReportService.renderStatic(
                "reports/beginner/03_parameters_greeting.jrxml", params, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "greeting");
    }

    /** http://localhost:8080/api/reports/beginner/text-formatting?format=pdf */
    @GetMapping("/api/reports/beginner/text-formatting")
    public ResponseEntity<byte[]> logoAndTextFormatting(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderStatic(
                "reports/beginner/04_logo_and_text_formatting.jrxml", null, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "text-formatting");
    }
}
