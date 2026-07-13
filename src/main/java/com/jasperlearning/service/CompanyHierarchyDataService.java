package com.jasperlearning.service;

import com.jasperlearning.dto.*;
import com.jasperlearning.entity.Company;
import com.jasperlearning.entity.Employee;
import com.jasperlearning.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Builds the 4-level nested projection (Company -> Department -> Employee ->
 * SalaryHistory) used by the advanced "multiple levels of nested data" and
 * "dataset inside another dataset" examples.
 *
 * This is deliberately assembled in Java rather than via a single SQL join:
 * nested datasets in JasperReports expect each level to be its own
 * List/Collection field on the parent bean, which maps naturally onto a tree
 * of DTOs but not onto a flat SQL result set.
 */
@Service
@RequiredArgsConstructor
public class CompanyHierarchyDataService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<CompanyHierarchyDto> findAllForReport() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream().map(this::toDto).toList();
    }

    private CompanyHierarchyDto toDto(Company company) {
        List<DepartmentHierarchyDto> departments = company.getDepartments().stream()
                .map(dept -> {
                    List<Employee> employees = dept.getEmployees();
                    BigDecimal totalSalary = employees.stream()
                            .map(Employee::getSalary)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new DepartmentHierarchyDto(
                            dept.getName(),
                            employees.stream()
                                    .map(emp -> new EmployeeHierarchyDto(
                                            emp.getFullName(),
                                            emp.getHireDate(),
                                            emp.getSalary(),
                                            emp.getSalaryHistory().stream()
                                                    .map(sh -> new SalaryHistoryDto(
                                                            sh.getEffectiveDate(),
                                                            sh.getAmount(),
                                                            sh.getReason()))
                                                    .toList()))
                                    .toList(),
                            employees.size(),
                            totalSalary);
                })
                .toList();

        return new CompanyHierarchyDto(company.getName(), company.getAddress(), company.getFoundedYear(), departments);
    }
}
