package com.jasperlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Flat, report-friendly projection of Employee + Department + Company.
 *
 * WHY a separate DTO instead of feeding JasperReports the JPA entity directly:
 * lazy-loaded associations (department, company) would throw
 * LazyInitializationException once the Hibernate session closes, which by the
 * time JasperFillManager runs, it usually has. Flattening inside the
 * transactional service method avoids that entirely and keeps the report
 * template decoupled from the persistence model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeReportDto {
    private String fullName;
    private String email;
    private String departmentName;
    private String companyName;
    private LocalDate hireDate;
    private BigDecimal salary;
}
