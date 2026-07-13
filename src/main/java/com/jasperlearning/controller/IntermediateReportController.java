package com.jasperlearning.controller;

import com.jasperlearning.dto.EmployeeReportDto;
import com.jasperlearning.report.GeneratedReport;
import com.jasperlearning.report.JasperReportService;
import com.jasperlearning.report.ReportFormat;
import com.jasperlearning.service.CompanyHierarchyDataService;
import com.jasperlearning.service.EmployeeDataService;
import com.jasperlearning.service.OrderDataService;
import com.jasperlearning.util.ReportHttpSupport;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * INTERMEDIATE TIER
 * =================
 * Grouping/sorting, page headers & footers across multiple pages, images
 * generated per row, native charts, and both flavors of subreport (data
 * hand-off, and parameter hand-off).
 */
@RestController
@RequiredArgsConstructor
public class IntermediateReportController {

    private final JasperReportService jasperReportService;
    private final EmployeeDataService employeeDataService;
    private final OrderDataService orderDataService;
    private final CompanyHierarchyDataService companyHierarchyDataService;

    /** http://localhost:8080/api/reports/intermediate/grouping?format=pdf */
    @GetMapping("/api/reports/intermediate/grouping")
    public ResponseEntity<byte[]> groupingAndSorting(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/intermediate/01_grouping_sorting.jrxml", null,
                employeeDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "grouping-sorting");
    }

    /** http://localhost:8080/api/reports/intermediate/multipage?format=pdf */
    @GetMapping("/api/reports/intermediate/multipage")
    public ResponseEntity<byte[]> headersFootersMultiPage(@RequestParam(defaultValue = "pdf") String format) {
        // Duplicate the (small) sample dataset so this demo reliably spans
        // multiple pages regardless of how many rows sql/data.sql happens to seed.
        List<EmployeeReportDto> employees = employeeDataService.findAllForReport();
        List<EmployeeReportDto> repeated = new ArrayList<>(employees);
        repeated.addAll(employees);

        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/intermediate/02_headers_footers_multipage.jrxml", null,
                repeated, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "headers-footers-multipage");
    }

    /** http://localhost:8080/api/reports/intermediate/dynamic-images?format=pdf */
    @GetMapping("/api/reports/intermediate/dynamic-images")
    public ResponseEntity<byte[]> dynamicImages(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/intermediate/03_dynamic_images.jrxml", null,
                employeeDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "dynamic-images");
    }

    /** http://localhost:8080/api/reports/intermediate/charts?format=pdf */
    @GetMapping("/api/reports/intermediate/charts")
    public ResponseEntity<byte[]> charts(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/intermediate/04_charts.jrxml", null,
                employeeDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "charts");
    }

    /** http://localhost:8080/api/reports/intermediate/orders?format=pdf */
    @GetMapping("/api/reports/intermediate/orders")
    public ResponseEntity<byte[]> subreportMasterDetail(@RequestParam(defaultValue = "pdf") String format) {
        JasperReport subreport = jasperReportService.compile(
                "reports/intermediate/subreports/order_items_subreport.jrxml");

        Map<String, Object> params = new HashMap<>();
        params.put("orderItemsSubreport", subreport);

        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/intermediate/05_subreport_master_detail.jrxml", params,
                orderDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "orders-with-items");
    }

    /** http://localhost:8080/api/reports/intermediate/company-overview?highlightThreshold=15000&format=pdf */
    @GetMapping("/api/reports/intermediate/company-overview")
    public ResponseEntity<byte[]> subreportParameterPassing(
            @RequestParam(required = false) BigDecimal highlightThreshold,
            @RequestParam(defaultValue = "pdf") String format) {

        JasperReport subreport = jasperReportService.compile(
                "reports/intermediate/subreports/department_headcount_subreport.jrxml");

        Map<String, Object> params = new HashMap<>();
        params.put("departmentHeadcountSubreport", subreport);
        if (highlightThreshold != null) params.put("highlightThreshold", highlightThreshold);

        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/intermediate/06_subreport_parameter_passing.jrxml", params,
                companyHierarchyDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "company-overview");
    }
}
