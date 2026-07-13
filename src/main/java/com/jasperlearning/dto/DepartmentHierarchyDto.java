package com.jasperlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentHierarchyDto {
    private String name;
    private List<EmployeeHierarchyDto> employees;

    /**
     * Pre-aggregated in Java (CompanyHierarchyDataService) rather than inside
     * the .jrxml: these two feed the intermediate "passing parameters between
     * reports" subreport, which only needs the per-department totals, not the
     * full nested employee list that CompanyHierarchyDto carries for the
     * advanced multi-level nesting example.
     */
    private Integer employeeCount;
    private BigDecimal totalSalary;
}
