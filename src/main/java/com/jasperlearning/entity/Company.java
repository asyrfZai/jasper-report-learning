package com.jasperlearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Root of the "org chart" domain used by the grouping / master-detail / nested
 * dataset examples: Company -> Department -> Employee -> SalaryHistory.
 */
@Entity
@Table(name = "company")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private Integer foundedYear;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Department> departments = new ArrayList<>();
}
