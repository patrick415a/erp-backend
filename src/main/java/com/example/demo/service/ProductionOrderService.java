package com.example.demo.service;

import com.example.demo.entity.ProductionOrder;
import com.example.demo.repository.ProductionOrderRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductionOrderService {

    private static final String NEXT_SEQUENCE = """
        INSERT INTO production_order_counter (counter_id, last_sequence)
        VALUES (1, CASE
            WHEN EXISTS (SELECT 1 FROM production_order_counter WHERE counter_id = 1) THEN 1
            ELSE (
                SELECT COALESCE(MAX(CAST(SUBSTR(order_id, 10) AS INTEGER)), 0) + 1
                FROM production_orders
                WHERE order_id GLOB 'PRD-[0-9][0-9][0-9][0-9]-[0-9]*'
                  AND SUBSTR(order_id, 10) NOT GLOB '*[^0-9]*'
            )
        END)
        ON CONFLICT (counter_id) DO UPDATE SET last_sequence = last_sequence + 1
        RETURNING last_sequence
        """;

    private final ProductionOrderRepository productionOrderRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<ProductionOrder> getAllOrders(){
        return productionOrderRepository.findAll();
    }

    @Transactional
    public ProductionOrder createOrder(ProductionOrder order) {
        long sequence = jdbcTemplate.queryForObject(NEXT_SEQUENCE, Long.class);
        order.setId(String.format(Locale.ROOT, "PRD-%d-%04d", Year.now().getValue(), sequence));
        entityManager.persist(order);
        return order;
    }

    @Transactional
    public ProductionOrder updateOrder(String id, ProductionOrder updatedOrder) {
        ProductionOrder order = productionOrderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("생산 오더가 없습니다: " + id));

        order.setProductName(updatedOrder.getProductName());
        order.setQuantity(updatedOrder.getQuantity());
        order.setStatus(updatedOrder.getStatus());
        order.setProgress(updatedOrder.getProgress());
        order.setDueDate(updatedOrder.getDueDate());

        return productionOrderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(String id) {
        if (!productionOrderRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 생산 오더입니다: " + id
            );
        }

        productionOrderRepository.deleteById(id);
    }
}
