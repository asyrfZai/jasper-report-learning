package com.jasperlearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Leaf level of the 4-level nested domain: Company -> Department -> Employee -> SalaryHistory.
 * Used by the "multiple levels of nested data" advanced example.
 */
@Entity
@Table(name = "salary_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate effectiveDate;

    private BigDecimal amount;

    private String reason;
}
