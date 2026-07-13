package com.jasperlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Fully flattened order-item row (customer + product category + line total) - built for the crosstab example. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemFlatDto {
    private String customerName;
    private String category;
    private BigDecimal lineTotal;
}
