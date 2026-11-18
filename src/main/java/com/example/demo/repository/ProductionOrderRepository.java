package com.example.demo.repository;

import com.example.demo.entity.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, String> {
}
