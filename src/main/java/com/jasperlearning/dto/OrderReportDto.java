package com.jasperlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Master record for the subreport example: one order (master) with a list of
 * line items (detail) that gets handed to a subreport as its own data source.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportDto {
    private Long orderId;
    private String customerName;
    private LocalDate orderDate;
    private List<OrderItemReportDto> items;

    /** Pre-summed in Java so the "complex master-detail" example can group/total by it without re-deriving from items in the .jrxml. */
    private BigDecimal orderTotal;
}
