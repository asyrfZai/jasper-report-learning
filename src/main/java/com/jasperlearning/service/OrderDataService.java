package com.jasperlearning.service;

import com.jasperlearning.dto.OrderItemFlatDto;
import com.jasperlearning.dto.OrderItemReportDto;
import com.jasperlearning.dto.OrderReportDto;
import com.jasperlearning.entity.CustomerOrder;
import com.jasperlearning.entity.OrderItem;
import com.jasperlearning.repository.CustomerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/** Backs the subreport and crosstab examples (order -> line items -> product). */
@Service
@RequiredArgsConstructor
public class OrderDataService {

    private final CustomerOrderRepository customerOrderRepository;

    @Transactional(readOnly = true)
    public List<OrderReportDto> findAllForReport() {
        List<CustomerOrder> orders = customerOrderRepository.findAll();
        return orders.stream().map(this::toDto).toList();
    }

    /** Same rows as findAllForReport(), pre-sorted by customer - required by the
     *  "complex master-detail" example's <group>/<sortField> (see 06_complex_master_detail_statement.jrxml). */
    @Transactional(readOnly = true)
    public List<OrderReportDto> findAllForReportSortedByCustomer() {
        List<CustomerOrder> orders = customerOrderRepository.findAll();
        return orders.stream()
                .sorted(Comparator.comparing(CustomerOrder::getCustomerName).thenComparing(CustomerOrder::getOrderDate))
                .map(this::toDto)
                .toList();
    }

    private OrderReportDto toDto(CustomerOrder order) {
        List<OrderItemReportDto> items = order.getItems().stream()
                .map(item -> new OrderItemReportDto(
                        item.getProduct().getName(),
                        item.getProduct().getCategory(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()))
                .toList();
        BigDecimal orderTotal = order.getItems().stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderReportDto(order.getId(), order.getCustomerName(), order.getOrderDate(), items, orderTotal);
    }

    /** Fully flattened (order x item) rows - one per line item, joined against its order's customer - for the crosstab example. */
    @Transactional(readOnly = true)
    public List<OrderItemFlatDto> findAllItemsFlatForReport() {
        List<CustomerOrder> orders = customerOrderRepository.findAll();
        return orders.stream()
                .flatMap(order -> order.getItems().stream()
                        .map(item -> new OrderItemFlatDto(
                                order.getCustomerName(),
                                item.getProduct().getCategory(),
                                item.getLineTotal())))
                .toList();
    }
}
