package com.jasperlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Root of the 4-level nested projection: Company -> Department -> Employee -> SalaryHistory.
 *
 * This shape backs the "multiple levels of nested data" and "dataset inside
 * another dataset" advanced examples. Each nested List becomes, inside the
 * .jrxml, a sub-dataset fed via a JRBeanCollectionDataSource created with a
 * "new" data source expression such as:
 *
 *   new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($F{departments})
 *
 * so a List element's List field can flow into a nested <subDataset>.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyHierarchyDto {
    private String name;
    private String address;
    private Integer foundedYear;
    private List<DepartmentHierarchyDto> departments;
}
