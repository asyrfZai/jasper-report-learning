package com.jasperlearning.service;

import com.jasperlearning.dto.EmployeeReportDto;
import com.jasperlearning.entity.Employee;
import com.jasperlearning.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Flattens the JPA graph into report-ready DTOs.
 *
 * The @Transactional boundary matters: it keeps the Hibernate session open
 * long enough for department/company (LAZY) to be traversed here, so the
 * JasperReports layer only ever sees plain, already-resolved JavaBeans.
 */
@Service
@RequiredArgsConstructor
public class EmployeeDataService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<EmployeeReportDto> findAllForReport() {
        List<Employee> employees = employeeRepository.findAllByOrderByDepartmentIdAscLastNameAsc();
        return employees.stream()
                .map(e -> new EmployeeReportDto(
                        e.getFullName(),
                        e.getEmail(),
                        e.getDepartment().getName(),
                        e.getDepartment().getCompany().getName(),
                        e.getHireDate(),
                        e.getSalary()))
                .toList();
    }
}
