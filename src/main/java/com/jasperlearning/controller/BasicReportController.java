package com.jasperlearning.controller;

import com.jasperlearning.report.GeneratedReport;
import com.jasperlearning.report.JasperReportService;
import com.jasperlearning.report.ReportFormat;
import com.jasperlearning.repository.ProductRepository;
import com.jasperlearning.service.EmployeeDataService;
import com.jasperlearning.util.ReportHttpSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * BASIC TIER
 * ==========
 * Introduces real, repeating data: JavaBean collections (renderWithBeans),
 * an engine-executed SQL query (renderFromSql), the Table/List components,
 * variables/expressions, formatting patterns, and conditional style/printing.
 */
@RestController
@RequiredArgsConstructor
public class BasicReportController {

    private final JasperReportService jasperReportService;
    private final EmployeeDataService employeeDataService;
    private final ProductRepository productRepository;

    /** http://localhost:8080/api/reports/basic/employees-javabean?format=pdf */
    @GetMapping("/api/reports/basic/employees-javabean")
    public ResponseEntity<byte[]> javaBeanDataSource(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/basic/01_javabean_datasource.jrxml", null,
                employeeDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "employees-javabean");
    }

    /** http://localhost:8080/api/reports/basic/products-sql?minUnitPrice=50&format=pdf */
    @GetMapping("/api/reports/basic/products-sql")
    public ResponseEntity<byte[]> sqlDataSource(
            @RequestParam(required = false) BigDecimal minUnitPrice,
            @RequestParam(defaultValue = "pdf") String format) {

        Map<String, Object> params = new HashMap<>();
        if (minUnitPrice != null) params.put("minUnitPrice", minUnitPrice);

        GeneratedReport report = jasperReportService.renderFromSql(
                "reports/basic/02_sql_datasource.jrxml", params, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "products-sql");
    }

    /** http://localhost:8080/api/reports/basic/products-table?format=pdf */
    @GetMapping("/api/reports/basic/products-table")
    public ResponseEntity<byte[]> tableComponent(@RequestParam(defaultValue = "pdf") String format) {
        Map<String, Object> params = Map.of("products", productRepository.findAll());
        GeneratedReport report = jasperReportService.renderStatic(
                "reports/basic/03_table_component.jrxml", params, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "products-table");
    }

    /** http://localhost:8080/api/reports/basic/products-cards?format=pdf */
    @GetMapping("/api/reports/basic/products-cards")
    public ResponseEntity<byte[]> listComponent(@RequestParam(defaultValue = "pdf") String format) {
        Map<String, Object> params = Map.of("products", productRepository.findAll());
        GeneratedReport report = jasperReportService.renderStatic(
                "reports/basic/04_list_component.jrxml", params, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "products-cards");
    }

    /** http://localhost:8080/api/reports/basic/payroll-variables?format=pdf */
    @GetMapping("/api/reports/basic/payroll-variables")
    public ResponseEntity<byte[]> variablesAndExpressions(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/basic/05_variables_expressions.jrxml", null,
                employeeDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "payroll-variables");
    }

    /** http://localhost:8080/api/reports/basic/formatting-reference?format=pdf */
    @GetMapping("/api/reports/basic/formatting-reference")
    public ResponseEntity<byte[]> dateAndNumberFormatting(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderStatic(
                "reports/basic/06_date_number_formatting.jrxml", null, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "formatting-reference");
    }

    /** http://localhost:8080/api/reports/basic/salary-review?minSalary=6500&format=pdf */
    @GetMapping("/api/reports/basic/salary-review")
    public ResponseEntity<byte[]> conditionalStylesAndPrinting(
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(defaultValue = "pdf") String format) {

        Map<String, Object> params = new HashMap<>();
        if (minSalary != null) params.put("minSalaryToShow", minSalary);

        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/basic/07_conditional_styles_and_printing.jrxml", params,
                employeeDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "salary-review");
    }
}
