package com.jasperlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeHierarchyDto {
    private String fullName;
    private LocalDate hireDate;
    private BigDecimal currentSalary;
    private List<SalaryHistoryDto> salaryHistory;
}
