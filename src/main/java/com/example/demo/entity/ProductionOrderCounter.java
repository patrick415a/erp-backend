package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRODUCTION_ORDER_COUNTER")
public class ProductionOrderCounter {
    @Id
    @Column(name = "COUNTER_ID")
    private Integer id;

    @Column(name = "LAST_SEQUENCE", nullable = false)
    private Long lastSequence;
}
