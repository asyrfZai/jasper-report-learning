package com.jasperlearning.repository;

import com.jasperlearning.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {
    List<SalaryHistory> findByEmployeeIdOrderByEffectiveDateAsc(Long employeeId);
}
