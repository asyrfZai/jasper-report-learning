package com.jasperlearning.controller;

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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ADVANCED TIER
 * =============
 * Every example that needs a pre-compiled child (subreport, nested
 * subreport, table-inside-subreport, SQL-connection subreport) compiles it
 * up front via jasperReportService.compile(...) and threads the resulting
 * JasperReport object through as a parameter - see each method's comment for
 * exactly which parameters flow where.
 */
@RestController
@RequiredArgsConstructor
public class AdvancedReportController {

    private final JasperReportService jasperReportService;
    private final EmployeeDataService employeeDataService;
    private final OrderDataService orderDataService;
    private final CompanyHierarchyDataService companyHierarchyDataService;
    private final DataSource dataSource;

    /** http://localhost:8080/api/reports/advanced/nested-subreports?format=pdf */
    @GetMapping("/api/reports/advanced/nested-subreports")
    public ResponseEntity<byte[]> nestedSubreports(@RequestParam(defaultValue = "pdf") String format) {
        JasperReport employeeSubreport = jasperReportService.compile(
                "reports/advanced/subreports/employee_list_subreport.jrxml");
        JasperReport departmentSubreport = jasperReportService.compile(
                "reports/advanced/subreports/department_with_employees_subreport.jrxml");

        Map<String, Object> params = new HashMap<>();
        params.put("employeeListSubreport", employeeSubreport);
        params.put("departmentSubreport", departmentSubreport);

        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/advanced/01_nested_subreports.jrxml", params,
                companyHierarchyDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "nested-subreports");
    }

    /** http://localhost:8080/api/reports/advanced/nested-datasets?format=pdf */
    @GetMapping("/api/reports/advanced/nested-datasets")
    public ResponseEntity<byte[]> nestedDatasetsMultiLevel(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/advanced/02_nested_datasets_multilevel.jrxml", null,
                companyHierarchyDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "nested-datasets-multilevel");
    }

    /** http://localhost:8080/api/reports/advanced/list-in-table?format=pdf */
    @GetMapping("/api/reports/advanced/list-in-table")
    public ResponseEntity<byte[]> listInsideTable(@RequestParam(defaultValue = "pdf") String format) {
        Map<String, Object> params = Map.of("orders", orderDataService.findAllForReport());
        GeneratedReport report = jasperReportService.renderStatic(
                "reports/advanced/03_list_inside_table.jrxml", params, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "list-inside-table");
    }

    /** http://localhost:8080/api/reports/advanced/table-in-subreport?format=pdf */
    @GetMapping("/api/reports/advanced/table-in-subreport")
    public ResponseEntity<byte[]> tableInsideSubreport(@RequestParam(defaultValue = "pdf") String format) {
        JasperReport itemsTableSubreport = jasperReportService.compile(
                "reports/advanced/subreports/order_items_table_subreport.jrxml");

        Map<String, Object> params = Map.of("itemsTableSubreport", itemsTableSubreport);

        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/advanced/04_tables_inside_subreports.jrxml", params,
                orderDataService.findAllForReport(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "table-inside-subreport");
    }

    /** http://localhost:8080/api/reports/advanced/crosstab?format=pdf */
    @GetMapping("/api/reports/advanced/crosstab")
    public ResponseEntity<byte[]> crosstab(@RequestParam(defaultValue = "pdf") String format) {
        Map<String, Object> params = Map.of("flatItems", orderDataService.findAllItemsFlatForReport());
        GeneratedReport report = jasperReportService.renderStatic(
                "reports/advanced/05_crosstab.jrxml", params, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "crosstab");
    }

    /** http://localhost:8080/api/reports/advanced/customer-statement?format=pdf */
    @GetMapping("/api/reports/advanced/customer-statement")
    public ResponseEntity<byte[]> complexMasterDetail(@RequestParam(defaultValue = "pdf") String format) {
        JasperReport itemsSubreport = jasperReportService.compile(
                "reports/intermediate/subreports/order_items_subreport.jrxml");

        Map<String, Object> params = Map.of("orderItemsSubreport", itemsSubreport);

        GeneratedReport report = jasperReportService.renderWithBeans(
                "reports/advanced/06_complex_master_detail_statement.jrxml", params,
                orderDataService.findAllForReportSortedByCustomer(), ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "customer-statement");
    }

    /** http://localhost:8080/api/reports/advanced/dynamic-layout?viewMode=BOTH&format=pdf  (viewMode: TABLE | CHART | BOTH) */
    @GetMapping("/api/reports/advanced/dynamic-layout")
    public ResponseEntity<byte[]> dynamicLayout(
            @RequestParam(defaultValue = "BOTH") String viewMode,
            @RequestParam(defaultValue = "pdf") String format) {

        Map<String, Object> params = Map.of(
                "viewMode", viewMode.toUpperCase(),
                "employees", employeeDataService.findAllForReport());

        GeneratedReport report = jasperReportService.renderStatic(
                "reports/advanced/07_dynamic_layout.jrxml", params, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "dynamic-layout");
    }

    /** http://localhost:8080/api/reports/advanced/multiple-data-sources?format=pdf */
    @GetMapping("/api/reports/advanced/multiple-data-sources")
    public ResponseEntity<byte[]> multipleDataSources(@RequestParam(defaultValue = "pdf") String format) {
        JasperReport salesSubreport = jasperReportService.compile(
                "reports/advanced/subreports/sales_summary_sql_subreport.jrxml");

        // The connection only needs to stay open for the synchronous fill() call
        // inside renderWithBeans - export works purely off the resulting
        // JasperPrint, so it's safe to close it as soon as rendering returns.
        try (Connection connection = dataSource.getConnection()) {
            Map<String, Object> params = new HashMap<>();
            params.put("salesConnection", connection);
            params.put("salesSummarySubreport", salesSubreport);

            GeneratedReport report = jasperReportService.renderWithBeans(
                    "reports/advanced/08_multiple_data_sources.jrxml", params,
                    employeeDataService.findAllForReport(), ReportFormat.from(format));
            return ReportHttpSupport.toResponse(report, "multiple-data-sources");
        } catch (SQLException e) {
            throw new com.jasperlearning.report.ReportGenerationException("Failed to open sales connection", e);
        }
    }

    /** http://localhost:8080/api/reports/advanced/custom-datasource?format=pdf */
    @GetMapping("/api/reports/advanced/custom-datasource")
    public ResponseEntity<byte[]> collectionsMapsCustomDataSource(@RequestParam(defaultValue = "pdf") String format) {
        List<Map<String, Object>> mapRows = List.of(
                Map.of("productName", "Wireless Mouse", "quantity", 42),
                Map.of("productName", "Mechanical Keyboard", "quantity", 17),
                Map.of("productName", "27\" Monitor", "quantity", 9),
                Map.of("productName", "Standing Desk", "quantity", 4));

        Map<String, Object> params = Map.of("mapRows", mapRows);

        GeneratedReport report = jasperReportService.renderStatic(
                "reports/advanced/09_collections_maps_custom_datasource.jrxml", params, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "custom-datasource");
    }

    /**
     * http://localhost:8080/api/reports/advanced/nested-sql-datasets?format=pdf
     *
     * Three SQL <subDataset>s nested in ONE .jrxml. renderFromSql fills the report
     * with the H2 Connection; each nested dataset reuses $P{REPORT_CONNECTION} and
     * receives the parent row's id via <datasetParameter>, so the child query is
     * filtered by the parent (company -> department -> employee).
     */
    @GetMapping("/api/reports/advanced/nested-sql-datasets")
    public ResponseEntity<byte[]> nestedSqlDatasets(@RequestParam(defaultValue = "pdf") String format) {
        GeneratedReport report = jasperReportService.renderFromSql(
                "reports/advanced/10_nested_sql_datasets.jrxml", null, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "nested-sql-datasets");
    }

    /**
     * http://localhost:8080/api/reports/advanced/deep-nested-sql-datasets?minSalary=6000&format=pdf
     *
     * Bigger version: 5 SQL datasets in one .jrxml - one aggregate (JOIN + GROUP BY)
     * plus a 4-level nested drill-down (company -> department -> employee -> salary
     * history). $P{minSalary} is threaded down through every dataset level and used
     * to filter the employee query.
     */
    @GetMapping("/api/reports/advanced/deep-nested-sql-datasets")
    public ResponseEntity<byte[]> deepNestedSqlDatasets(
            @RequestParam(required = false) java.math.BigDecimal minSalary,
            @RequestParam(defaultValue = "pdf") String format) {

        Map<String, Object> params = new HashMap<>();
        if (minSalary != null) params.put("minSalary", minSalary);

        GeneratedReport report = jasperReportService.renderFromSql(
                "reports/advanced/11_deep_nested_sql_datasets.jrxml", params, ReportFormat.from(format));
        return ReportHttpSupport.toResponse(report, "deep-nested-sql-datasets");
    }
}
