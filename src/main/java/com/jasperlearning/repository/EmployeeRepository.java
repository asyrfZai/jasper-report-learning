package com.jasperlearning.repository;

import com.jasperlearning.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findAllByOrderByDepartmentIdAscLastNameAsc();
}
