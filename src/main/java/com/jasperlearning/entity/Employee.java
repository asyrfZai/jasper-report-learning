package com.jasperlearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDate hireDate;

    private BigDecimal salary;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<SalaryHistory> salaryHistory = new ArrayList<>();

    /**
     * Convenience getter used directly as a JasperReports field (JRBeanCollectionDataSource
     * resolves fields via JavaBean getters, including derived ones like this).
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
