package com.jasperlearning.controller;

import com.jasperlearning.dto.EmployeeReportDto;
import com.jasperlearning.report.JasperReportService;
import com.jasperlearning.report.ReportFormat;
import com.jasperlearning.service.EmployeeDataService;
import com.jasperlearning.util.ReportHttpSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Employee report, routed through the single JasperReportService like every
 * other report.
 *
 *   GET /api/reports/employees?format=pdf
 *   GET /api/reports/employees?format=xlsx&title=Acme%20Payroll
 *
 * Flow: data service builds the JavaBean list -> params Map -> service compiles,
 * fills (JRBeanCollectionDataSource) and exports -> stream bytes back.
 */
@RestController
public class EmployeeReportController {

    private static final String TEMPLATE = "reports/employee_report.jrxml";

    private final JasperReportService jasperReportService;
    private final EmployeeDataService employeeDataService;

    public EmployeeReportController(JasperReportService jasperReportService,
                                    EmployeeDataService employeeDataService) {
        this.jasperReportService = jasperReportService;
        this.employeeDataService = employeeDataService;
    }

    @GetMapping("/api/reports/employees")
    public ResponseEntity<byte[]> employees(@RequestParam(defaultValue = "pdf") String format,
                                            @RequestParam(required = false) String title) {
        List<EmployeeReportDto> employees = employeeDataService.findAllForReport();

        Map<String, Object> params = new HashMap<>();
        if (title != null) {
            params.put("REPORT_TITLE", title);
        }

        return ReportHttpSupport.toResponse(
                jasperReportService.renderWithBeans(TEMPLATE, params, employees, ReportFormat.from(format)),
                "employee_report");
    }
}
