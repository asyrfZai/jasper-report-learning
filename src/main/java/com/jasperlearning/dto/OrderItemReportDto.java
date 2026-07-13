package com.jasperlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemReportDto {
    private String productName;
    private String category;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
